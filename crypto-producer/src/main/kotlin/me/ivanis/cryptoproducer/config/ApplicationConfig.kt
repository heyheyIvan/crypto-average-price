package me.ivanis.cryptoproducer.config

import java.util.*


class ApplicationConfig(properties: Properties) {
    val bootstrapServers: String = properties.getProperty("bootstrap.servers", "kafka-dev")
    val topic: String = properties.getProperty("topic", "btc_usd")

    override fun toString(): String {
        return "ApplicationConfig(bootstrapServers='$bootstrapServers', topic='$topic')"
    }

    companion object {
        fun loadAppConfig(resourceName: String): ApplicationConfig {
            val appProperties = Properties()
            appProperties.load(ApplicationConfig::class.java.classLoader.getResourceAsStream(resourceName))

            for (property in appProperties.stringPropertyNames()) {
                val value = appProperties.getProperty(property)
                val envVariable = Regex("\\$\\{(.*)}").find(value)?.groups?.get(1)?.value

                if (envVariable != null) {
                    val envValue: String = System.getenv(envVariable)
                            ?: throw IllegalArgumentException("Missing environment variable $envVariable")
                    appProperties.setProperty(property, envValue)
                }
            }
            return ApplicationConfig(appProperties)
        }
    }

}