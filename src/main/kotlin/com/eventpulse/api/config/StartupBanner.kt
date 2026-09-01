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
        val publicUrl = environment.getProperty("APP_PUBLIC_URL")
        val railwayPublicDomain = environment.getProperty("RAILWAY_PUBLIC_DOMAIN")
        val railwayEnvironment = environment.getProperty("RAILWAY_ENVIRONMENT")
        val banner = StartupAccessInfo.formatBanner(
            port = serverProperties.port,
            contextPath = serverProperties.servlet.contextPath,
            activeProfiles = environment.activeProfiles,
            publicUrl = publicUrl,
            railwayPublicDomain = railwayPublicDomain,
            railwayEnvironment = railwayEnvironment,
        )
        val port = StartupAccessInfo.resolvePort(serverProperties.port)
        val contextPath = StartupAccessInfo.resolveContextPath(serverProperties.servlet.contextPath)
        val base = StartupAccessInfo.baseUrl(
            port,
            contextPath,
            publicUrl = publicUrl,
            railwayPublicDomain = railwayPublicDomain,
            railwayEnvironment = railwayEnvironment,
        )

        println(banner)
        log.info(
            "EventPulse ready — health={} | swagger={} | repo={}",
            "$base/api/v1/health",
            "$base/swagger-ui.html",
            StartupAccessInfo.REPO_URL,
        )
    }
}
