package me.ivanis.cryptoproducer

import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange
import info.bitrich.xchangestream.hitbtc.HitbtcStreamingExchange
import info.bitrich.xchangestream.okcoin.OkCoinStreamingExchange
import me.ivanis.cryptoproducer.config.ApplicationConfig
import me.ivanis.cryptoproducer.model.MarketData
import me.ivanis.cryptoproducer.producer.CryptoProducer
import me.ivanis.cryptoproducer.support.MarketDataSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.IntegerSerializer
import org.knowm.xchange.Exchange
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass


object CryptoProducerApp {
    private val logger: Logger = LoggerFactory.getLogger("CryptoProducerApp")

    @JvmStatic
    fun main(args: Array<String>) {
        val appConfig = ApplicationConfig.loadAppConfig("application.properties")
        logger.info("crypto-producer with configuration:\n$appConfig")

        val exchanges: Map<String, KClass<out Exchange>> = mapOf(
                "hitbtc" to HitbtcStreamingExchange::class,
                "bitfinex" to BitfinexStreamingExchange::class,
                "okcoin" to OkCoinStreamingExchange::class
        )
        val cryptoProducer = CryptoProducer(exchanges, appConfig.topic, createKafkaProducer(appConfig))
        cryptoProducer.start()
    }

    private fun createKafkaProducer(applicationConfig: ApplicationConfig): KafkaProducer<Int, MarketData> {
        val props = HashMap<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = applicationConfig.bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = IntegerSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = MarketDataSerializer::class.java
        return KafkaProducer(props)
    }
}
