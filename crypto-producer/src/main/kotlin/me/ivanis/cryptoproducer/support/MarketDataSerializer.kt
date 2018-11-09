package me.ivanis.cryptoproducer.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.ivanis.cryptoproducer.model.MarketData
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class MarketDataSerializer(private val objectMapper: ObjectMapper = jacksonObjectMapper()) : Serializer<MarketData> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {

    }

    override fun serialize(topic: String?, data: MarketData?): ByteArray? {
        return if (data == null) {
            null
        } else {
            try {
                objectMapper.writeValueAsBytes(data)
            } catch (e: Exception) {
                throw SerializationException("Can't serialize data [$data] for topic [$topic]", e)
            }
        }
    }

    override fun close() {

    }

}