package com.eventpulse.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Service health checks")
class HealthController {

    @GetMapping
    @Operation(summary = "Liveness check")
    fun health(): ResponseEntity<Map<String, Any>> =
        ResponseEntity.ok(
            mapOf(
                "status" to "UP",
                "service" to "eventpulse-api",
                "timestamp" to Instant.now().toString(),
            ),
        )
}
