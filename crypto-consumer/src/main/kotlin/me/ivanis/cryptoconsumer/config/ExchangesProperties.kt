package me.ivanis.cryptoconsumer.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.math.BigDecimal


@Component
@ConfigurationProperties(prefix = "exchanges-configuration")
class ExchangesProperties {
    lateinit var exchanges: List<Exchange>

    fun coefficientMap(): Map<String, BigDecimal> {
        return exchanges.map { it.name to it.coefficient }.toMap()
    }

    override fun toString(): String {
        return "ExchangesProperties(exchanges=$exchanges)"
    }

    class Exchange {
        lateinit var name: String
        lateinit var coefficient: BigDecimal

        override fun toString(): String {
            return "Exchange(name='$name', coefficient=$coefficient)"
        }
    }
}

