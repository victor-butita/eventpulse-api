package com.eventpulse.api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun eventPulseOpenApi(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("EventPulse API")
                    .description("Backend API for creating and booking event tickets")
                    .version("v0")
                    .contact(Contact().name("EventPulse").url("https://github.com/victor-butita/eventpulse-api")),
            )
}
