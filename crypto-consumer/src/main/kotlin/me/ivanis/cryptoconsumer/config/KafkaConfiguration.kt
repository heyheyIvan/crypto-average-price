package me.ivanis.cryptoconsumer.config

import me.ivanis.cryptoconsumer.model.MarketData
import me.ivanis.cryptoconsumer.model.WeightedAveragePrice
import me.ivanis.cryptoconsumer.serde.JsonDeserializer
import me.ivanis.cryptoconsumer.serde.JsonSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.IntegerSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfiguration {

    @Bean
    @Autowired
    fun kafkaProducer(kafkaProperties: KafkaProperties): KafkaProducer<Int, WeightedAveragePrice> {
        val producerProps = HashMap<String, Any>()
        producerProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.producer.bootstrapServers
        producerProps[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = kafkaProperties.producer.transactionalId
        producerProps[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
        return KafkaProducer(producerProps, IntegerSerializer(), JsonSerializer<WeightedAveragePrice>())
    }

    @Bean
    @Autowired
    fun kafkaConsumer(kafkaProperties: KafkaProperties): KafkaConsumer<Int, MarketData> {
        val consumerProps = HashMap<String, Any>()
        consumerProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.consumer.bootstrapServers
        consumerProps[ConsumerConfig.GROUP_ID_CONFIG] = kafkaProperties.consumer.groupId
        consumerProps[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return KafkaConsumer(consumerProps, IntegerDeserializer(), JsonDeserializer(MarketData::class.java))
    }
}