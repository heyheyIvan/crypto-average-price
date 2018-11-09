package me.ivanis.cryptoconsumer.service

import me.ivanis.cryptoconsumer.config.ExchangesProperties
import me.ivanis.cryptoconsumer.config.KafkaProperties
import me.ivanis.cryptoconsumer.model.MarketData
import me.ivanis.cryptoconsumer.model.WeightedAveragePrice
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.mapdb.DBMaker
import org.mapdb.Serializer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration.ofSeconds
import javax.annotation.PostConstruct


@Service
class WeightedAveragePriceService(private val kafkaConsumer: KafkaConsumer<Int, MarketData>,
                                  private val kafkaProducer: KafkaProducer<Int, WeightedAveragePrice>,
                                  private val exchangesProperties: ExchangesProperties,
                                  kafkaProperties: KafkaProperties) {
    private val logger = LoggerFactory.getLogger(WeightedAveragePriceService::class.java)

    private val consumerTopic = kafkaProperties.consumer.topic
    private val consumerGroupId = kafkaProperties.consumer.groupId

    private val producerTopic = kafkaProperties.producer.topic


    private val partition0 = TopicPartition(consumerTopic, 0)
    private val db = DBMaker.fileDB("exchange-prices")
            .transactionEnable()
            .closeOnJvmShutdown()
            .make()
    private val stateMap = db.hashMap("state", Serializer.STRING, Serializer.BIG_DECIMAL)
            .createOrOpen()
    private val firstNotPersistedOffset = db.atomicLong("firstNotPersistedOffset", -1L)
            .createOrOpen()

    private val updateInterval: Long = 1000
    private var lastStateUpdate: Long = 0

    @PostConstruct
    fun init() {
        kafkaConsumer.subscribe(listOf(consumerTopic))
        kafkaProducer.initTransactions()

        logger.info("Restore previous state")

        val firstNotPersistedOffsetValue = firstNotPersistedOffset.get()

        if (firstNotPersistedOffsetValue != -1L) {
            val poll = kafkaConsumer.poll(ofSeconds(5))
            if (!poll.isEmpty) {
                val notConsumedRecords = poll.records(partition0)
                val firstNotConsumedOffset = notConsumedRecords.first().offset()

                kafkaConsumer.seek(partition0, firstNotPersistedOffsetValue)

                var restored = false

                while (true) {
                    val records = kafkaConsumer.poll(ofSeconds(100))
                    val partlyConsumedRecords = records.records(partition0)
                    for (record in partlyConsumedRecords) {
                        if (record.offset() == firstNotConsumedOffset) {
                            restored = true
                            break
                        }
                        val marketData = record.value()
                        stateMap[marketData.exchange] = marketData.price
                    }
                    if (restored) {
                        break
                    }
                    kafkaConsumer.seek(partition0, partlyConsumedRecords.last().offset() + 1)
                }
                kafkaConsumer.seek(partition0, firstNotConsumedOffset)
            }
        }
    }

    fun process() {
        try {
            while (true) {
                val records = kafkaConsumer.poll(ofSeconds(1000))
                val recordsFromPartition0 = records.records(partition0)
                recordsFromPartition0.forEach(::processRecord)
            }
        } catch (e: Exception) {
            logger.error("Error while process data", e)
            db.close()
        }
    }

    private fun processRecord(record: ConsumerRecord<Int, MarketData>) {
        val now = System.currentTimeMillis()

        if (now > lastStateUpdate + updateInterval) {
            firstNotPersistedOffset.set(record.offset())
            lastStateUpdate = now
            db.commit()
        }

        try {
            kafkaProducer.beginTransaction()
            val marketData = record.value()
            stateMap[marketData.exchange] = marketData.price

            if (stateMap.keys.size == exchangesProperties.exchanges.size) {
                val weightedAveragePrice = calculateWeightAveragePrice()
                logger.info("Sending $weightedAveragePrice to kafka to topic '$producerTopic'")
                kafkaProducer.send(ProducerRecord(producerTopic, weightedAveragePrice))
            }

            kafkaProducer.sendOffsetsToTransaction(
                    mapOf(partition0 to OffsetAndMetadata(record.offset() + 1)),
                    consumerGroupId
            )
            kafkaProducer.commitTransaction()
        } catch (e: Exception) {
            logger.error("Error during transaction", e)
            kafkaProducer.abortTransaction()
            throw e
        }
    }

    private fun calculateWeightAveragePrice(): WeightedAveragePrice {
        val coefficientBy = exchangesProperties.coefficientMap()
        val weightAverage = stateMap
                .map { (exchangeName, price) -> coefficientBy[exchangeName]!!.multiply(price) }
                .reduce { acc, price -> acc.plus(price) }
        return WeightedAveragePrice(weightAverage)
    }
}

