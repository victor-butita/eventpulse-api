package com.eventpulse.api.config

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class StartupBanner(
    private val environment: Environment,
    private val serverProperties: ServerProperties,
) {
    private val log = LoggerFactory.getLogger(StartupBanner::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        val port = serverProperties.port ?: 8080
        val contextPath = serverProperties.servlet.contextPath
            ?.takeIf { it.isNotBlank() && it != "/" }
            ?: ""
        val base = "http://localhost:$port$contextPath"
        val profiles = environment.activeProfiles
            .takeIf { it.isNotEmpty() }
            ?.joinToString(", ")
            ?: "default"

        val health = "$base/api/v1/health"
        val swagger = "$base/swagger-ui.html"
        val openApi = "$base/v3/api-docs"
        val repo = "https://github.com/victor-butita/eventpulse-api"

        val banner = """
            
            $CYAN━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$RESET
              $BOLD$GREEN● EventPulse API$RESET  $DIM— ready$RESET
            $CYAN━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$RESET

              $BOLD Local$RESET
                App ............. $YELLOW$base$RESET
                Health .......... $YELLOW$health$RESET

              $BOLD Docs$RESET
                Swagger UI ...... $YELLOW$swagger$RESET
                OpenAPI JSON .... $YELLOW$openApi$RESET

              $BOLD Project$RESET
                Repository ...... $YELLOW$repo$RESET
                Profile(s) ...... $GREEN$profiles$RESET
                Port ............ $GREEN$port$RESET

            $CYAN━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$RESET

            """.trimIndent()

        println(banner)
        log.info("EventPulse ready — health={} | swagger={} | repo={}", health, swagger, repo)
    }

    companion object {
        private const val RESET = "\u001B[0m"
        private const val BOLD = "\u001B[1m"
        private const val DIM = "\u001B[2m"
        private const val GREEN = "\u001B[32m"
        private const val CYAN = "\u001B[36m"
        private const val YELLOW = "\u001B[33m"
    }
}
