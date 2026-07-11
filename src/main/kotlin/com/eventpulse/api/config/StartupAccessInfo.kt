package com.eventpulse.api.config

/**
 * Builds the ready-state access links shown when the app starts.
 * Pure helpers so coverage does not depend on Spring Boot lifecycle quirks.
 */
object StartupAccessInfo {
    const val REPO_URL = "https://github.com/victor-butita/eventpulse-api"

    fun resolvePort(configuredPort: Int?): Int = configuredPort ?: 8080

    fun resolveContextPath(configuredPath: String?): String {
        val path = configuredPath?.trim().orEmpty()
        return when {
            path.isEmpty() || path == "/" -> ""
            path.startsWith("/") -> path
            else -> "/$path"
        }
    }

    fun resolveProfiles(activeProfiles: Array<String>): String =
        if (activeProfiles.isEmpty()) {
            "default"
        } else {
            activeProfiles.joinToString(", ")
        }

    fun baseUrl(port: Int, contextPath: String): String =
        "http://localhost:$port$contextPath"

    fun formatBanner(
        port: Int?,
        contextPath: String?,
        activeProfiles: Array<String>,
    ): String {
        val resolvedPort = resolvePort(port)
        val resolvedContext = resolveContextPath(contextPath)
        val base = baseUrl(resolvedPort, resolvedContext)
        val profiles = resolveProfiles(activeProfiles)
        val health = "$base/api/v1/health"
        val swagger = "$base/swagger-ui.html"
        val openApi = "$base/v3/api-docs"

        return """
            
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
                Repository ...... $YELLOW$REPO_URL$RESET
                Profile(s) ...... $GREEN$profiles$RESET
                Port ............ $GREEN$resolvedPort$RESET

            $CYAN━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$RESET

            """.trimIndent()
    }

    private const val RESET = "\u001B[0m"
    private const val BOLD = "\u001B[1m"
    private const val DIM = "\u001B[2m"
    private const val GREEN = "\u001B[32m"
    private const val CYAN = "\u001B[36m"
    private const val YELLOW = "\u001B[33m"
}
