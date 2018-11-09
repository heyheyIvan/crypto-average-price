package me.ivanis.cryptoproducer.producer

import info.bitrich.xchangestream.core.StreamingExchangeFactory
import me.ivanis.cryptoproducer.model.MarketData
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.knowm.xchange.Exchange
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.marketdata.Ticker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class CryptoProducer(
        private val exchanges: Map<String, KClass<out Exchange>>,
        private val topic: String,
        private val kafkaProducer: KafkaProducer<Int, MarketData>
) {
    private val logger: Logger = LoggerFactory.getLogger(CryptoProducer::class.java)

    fun start() {
        for ((exchangeName, exchangeClass) in exchanges) {
            val exchangeStream = StreamingExchangeFactory.INSTANCE.createExchange(exchangeClass.java.name)
            exchangeStream.connect().blockingAwait()
            exchangeStream.streamingMarketDataService
                    .getTicker(CurrencyPair.BTC_USD)
                    .subscribe { ticker: Ticker ->
                        val marketData = MarketData(exchangeName, ticker.last)
                        logger.info("Sending $marketData to kafka")
                        try {
                            kafkaProducer.send(ProducerRecord(topic, marketData))
                        } catch (e: Exception) {
                            logger.error("Error while sending message to kafka", e)
                        }
                    }
        }
    }
}