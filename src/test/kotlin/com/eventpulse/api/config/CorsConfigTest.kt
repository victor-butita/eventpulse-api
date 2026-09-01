package com.eventpulse.api.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.cors.CorsConfiguration

class CorsConfigTest {

    @Test
    fun patternsIncludeRailwayHttpsLocalAndVercel() {
        assertThat(CorsOrigins.patterns)
            .contains(OpenApiConfig.PRODUCTION_URL)
            .contains("http://localhost:*")
            .contains("https://*.vercel.app")
    }

    @Test
    fun corsSourceAllowsRegisterPreflightFromSwaggerOrigin() {
        val source = CorsConfig().corsConfigurationSource()
        val request = MockHttpServletRequest().apply {
            method = "OPTIONS"
            requestURI = "/api/auth/register"
            addHeader("Origin", OpenApiConfig.PRODUCTION_URL)
            addHeader("Access-Control-Request-Method", "POST")
        }

        val cors = source.getCorsConfiguration(request)
        assertThat(cors).isNotNull
        assertThat(cors!!.checkOrigin(OpenApiConfig.PRODUCTION_URL))
            .isEqualTo(OpenApiConfig.PRODUCTION_URL)
        assertThat(cors.checkHttpMethod(HttpMethod.POST)).contains(HttpMethod.POST)
        assertThat(cors.allowCredentials).isTrue()
    }

    @Test
    fun corsAllowsVercelPreviewOrigin() {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = CorsOrigins.patterns.toMutableList()
        }
        assertThat(config.checkOrigin("https://event-pulse-portal.vercel.app"))
            .isEqualTo("https://event-pulse-portal.vercel.app")
    }

    @Test
    fun corsRejectsUnknownOrigin() {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = CorsOrigins.patterns.toMutableList()
        }
        assertThat(config.checkOrigin("https://evil.example")).isNull()
    }
}
