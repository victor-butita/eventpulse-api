package com.eventpulse.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun eventPulseOpenApi(): OpenAPI {

        val schemeName = "bearerAuth"

        return OpenAPI()
            .servers(
                listOf(
                    Server()
                        .url(PRODUCTION_URL)
                        .description("Railway production (HTTPS)"),
                    Server()
                        .url("/")
                        .description("Current host"),
                )
            )
            .info(
                Info()
                    .title("EventPulse API")
                    .version("v1")
                    .description("Backend API for creating and booking event tickets")
                    .contact(
                        Contact()
                            .name("EventPulse")
                            .url("https://github.com/victor-butita/eventpulse-api")
                    )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        schemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
            .addSecurityItem(
                SecurityRequirement().addList(schemeName)
            )
    }

    companion object {
        const val PRODUCTION_URL =
            "https://eventpulse-api-production-259f.up.railway.app"
    }
}