package me.ivanis.cryptoconsumer.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "kafka")
class KafkaProperties {

    var producer: ProducerProperties = ProducerProperties()
    var consumer: ConsumerProperties = ConsumerProperties()

    class ProducerProperties {
        lateinit var bootstrapServers: String
        lateinit var topic: String
        lateinit var transactionalId: String

        override fun toString(): String {
            return "ProducerProperties(bootstrapServers='$bootstrapServers', topic='$topic', transactionalId='$transactionalId')"
        }
    }

    class ConsumerProperties {
        lateinit var bootstrapServers: String
        lateinit var topic: String
        lateinit var groupId: String

        override fun toString(): String {
            return "ConsumerProperties(bootstrapServers='$bootstrapServers', topic='$topic', groupId='$groupId')"
        }

    }

    override fun toString(): String {
        return "KafkaProperties(consumer=$consumer, producer=$producer)"
    }

}


