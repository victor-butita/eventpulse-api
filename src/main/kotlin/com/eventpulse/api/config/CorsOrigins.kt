package com.eventpulse.api.config

object CorsOrigins {
    val allowed = listOf(
        OpenApiConfig.PRODUCTION_URL,
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:4173",
        "http://localhost:8080",
        "http://127.0.0.1:8080",
    )
}
