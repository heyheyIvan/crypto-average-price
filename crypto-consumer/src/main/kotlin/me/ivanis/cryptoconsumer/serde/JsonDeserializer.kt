package me.ivanis.cryptoconsumer.serde

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import java.util.*


class JsonDeserializer<T>(private val targetType: Class<T>,
                          private val objectMapper: ObjectMapper = jacksonObjectMapper()) : Deserializer<T> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun deserialize(topic: String?, data: ByteArray?): T? {
        return if (data == null) {
            null
        } else {
            try {
                objectMapper.readValue(data, targetType)
            } catch (e: Exception) {
                throw SerializationException("Can't deserialize data [${Arrays.toString(data)}] from topic [$topic]", e)
            }
        }

    }

    override fun close() {
    }

}