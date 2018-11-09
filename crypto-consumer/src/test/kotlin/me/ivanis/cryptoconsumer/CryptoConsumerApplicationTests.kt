package me.ivanis.cryptoconsumer

import me.ivanis.cryptoconsumer.model.MarketData
import me.ivanis.cryptoconsumer.model.WeightedAveragePrice
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class CryptoConsumerApplicationTests {

    @MockBean
    lateinit var kafkaProducer: KafkaProducer<Int, WeightedAveragePrice>

    @MockBean
    lateinit var kafkaConsumer: KafkaConsumer<Int, MarketData>

    @MockBean
    lateinit var commandLineRunner: CommandLineRunner

    @Test
    fun contextLoads() {
    }

}
