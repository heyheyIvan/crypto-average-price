package me.ivanis.cryptoconsumer

import me.ivanis.cryptoconsumer.service.WeightedAveragePriceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@SpringBootApplication
class CryptoConsumerApplication {

    @Bean
    @Autowired
    fun commandLineRunner(taskExecutor: ThreadPoolTaskExecutor,
                          weightedAveragePriceService: WeightedAveragePriceService) = CommandLineRunner {
        taskExecutor.execute {
            weightedAveragePriceService.process()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<CryptoConsumerApplication>(*args)
}
