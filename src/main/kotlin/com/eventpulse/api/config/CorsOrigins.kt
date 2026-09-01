package com.eventpulse.api.config

object CorsOrigins {
    const val PORTAL_URL = "https://event-pulse-portal.vercel.app"

    val patterns = listOf(
        OpenApiConfig.PRODUCTION_URL,
        PORTAL_URL,
        "http://localhost:*",
        "http://127.0.0.1:*",
        "https://*.vercel.app",
    )
}
