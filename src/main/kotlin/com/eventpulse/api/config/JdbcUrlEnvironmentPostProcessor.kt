package com.eventpulse.api.config

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

/**
 * Rewrites Railway-style `mysql://` URLs before DataSource auto-configuration.
 */
class JdbcUrlEnvironmentPostProcessor : EnvironmentPostProcessor {

    override fun postProcessEnvironment(
        environment: ConfigurableEnvironment,
        application: SpringApplication,
    ) {
        val raw = environment.getProperty("SPRING_DATASOURCE_URL")
            ?: environment.getProperty("spring.datasource.url")
            ?: return

        val jdbcUrl = JdbcUrlNormalizer.normalize(raw) ?: return
        if (jdbcUrl == raw) {
            return
        }

        environment.propertySources.addFirst(
            MapPropertySource(
                PROPERTY_SOURCE_NAME,
                mapOf(
                    "spring.datasource.url" to jdbcUrl,
                    "SPRING_DATASOURCE_URL" to jdbcUrl,
                ),
            ),
        )
    }

    companion object {
        const val PROPERTY_SOURCE_NAME = "jdbcUrlNormalization"
    }
}
