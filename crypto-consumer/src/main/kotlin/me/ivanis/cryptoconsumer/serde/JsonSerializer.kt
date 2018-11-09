package me.ivanis.cryptoconsumer.serde

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class JsonSerializer<T>(private val objectMapper: ObjectMapper = jacksonObjectMapper()) : Serializer<T> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun serialize(topic: String?, data: T): ByteArray? {
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