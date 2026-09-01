package com.eventpulse.api.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.cors.CorsConfiguration

class CorsConfigTest {

    @Test
    fun allowedOriginsIncludeRailwayHttpsAndLocalVue() {
        assertThat(CorsOrigins.allowed)
            .contains(OpenApiConfig.PRODUCTION_URL)
            .contains("http://localhost:5173")
            .doesNotContain("http://eventpulse-api-production-259f.up.railway.app")
    }

    @Test
    fun corsSourceAllowsRegisterPreflightFromSwaggerOrigin() {
        val source = CorsConfig().corsConfigurationSource()
        val request = org.springframework.mock.web.MockHttpServletRequest().apply {
            method = "OPTIONS"
            requestURI = "/api/auth/register"
            addHeader("Origin", OpenApiConfig.PRODUCTION_URL)
            addHeader("Access-Control-Request-Method", "POST")
        }

        val cors = source.getCorsConfiguration(request)
        assertThat(cors).isNotNull
        assertThat(cors!!.checkOrigin(OpenApiConfig.PRODUCTION_URL))
            .isEqualTo(OpenApiConfig.PRODUCTION_URL)
        assertThat(cors.checkHttpMethod(org.springframework.http.HttpMethod.POST))
            .contains(org.springframework.http.HttpMethod.POST)
        assertThat(cors.allowCredentials).isTrue()
    }

    @Test
    fun corsRejectsUnknownOrigin() {
        val config = CorsConfiguration().apply {
            allowedOrigins = CorsOrigins.allowed
        }
        assertThat(config.checkOrigin("https://evil.example")).isNull()
    }
}
