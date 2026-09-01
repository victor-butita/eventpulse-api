package com.eventpulse.api.config

object CorsOrigins {
    val patterns = listOf(
        OpenApiConfig.PRODUCTION_URL,
        "http://localhost:*",
        "http://127.0.0.1:*",
        "https://*.vercel.app",
    )
}
