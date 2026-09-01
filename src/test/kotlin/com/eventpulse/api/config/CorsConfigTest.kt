package com.eventpulse.api.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.cors.CorsConfiguration

class CorsConfigTest {

    @Test
    fun patternsIncludeRailwayPortalAndVercel() {
        assertThat(CorsOrigins.patterns)
            .contains(OpenApiConfig.PRODUCTION_URL)
            .contains(CorsOrigins.PORTAL_URL)
            .contains("https://*.vercel.app")
    }

    @Test
    fun corsAllowsVercelPortalOrigin() {
        val cors = CorsConfig().corsConfigurationSource()
            .getCorsConfiguration(preflight(CorsOrigins.PORTAL_URL))

        assertThat(cors).isNotNull
        assertThat(cors!!.checkOrigin(CorsOrigins.PORTAL_URL))
            .isEqualTo(CorsOrigins.PORTAL_URL)
        assertThat(cors.checkHttpMethod(HttpMethod.POST)).contains(HttpMethod.POST)
        assertThat(cors.allowCredentials).isTrue()
    }

    @Test
    fun corsAllowsVercelPreviewOrigin() {
        val preview = "https://event-pulse-portal-git-main-victor.vercel.app"
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = CorsOrigins.patterns.toMutableList()
        }
        assertThat(config.checkOrigin(preview)).isEqualTo(preview)
    }

    @Test
    fun corsRejectsUnknownOrigin() {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = CorsOrigins.patterns.toMutableList()
        }
        assertThat(config.checkOrigin("https://evil.example")).isNull()
    }

    private fun preflight(origin: String) = MockHttpServletRequest().apply {
        method = "OPTIONS"
        requestURI = "/api/auth/register"
        addHeader("Origin", origin)
        addHeader("Access-Control-Request-Method", "POST")
    }
}
