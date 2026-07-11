package com.eventpulse.api.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StartupAccessInfoTest {

    @Test
    fun resolvePortUsesConfiguredValue() {
        assertThat(StartupAccessInfo.resolvePort(9090)).isEqualTo(9090)
    }

    @Test
    fun resolvePortDefaultsWhenNull() {
        assertThat(StartupAccessInfo.resolvePort(null)).isEqualTo(8080)
    }

    @Test
    fun resolveContextPathHandlesBlankAndRoot() {
        assertThat(StartupAccessInfo.resolveContextPath(null)).isEmpty()
        assertThat(StartupAccessInfo.resolveContextPath("")).isEmpty()
        assertThat(StartupAccessInfo.resolveContextPath("   ")).isEmpty()
        assertThat(StartupAccessInfo.resolveContextPath("/")).isEmpty()
    }

    @Test
    fun resolveContextPathNormalizesRelativePath() {
        assertThat(StartupAccessInfo.resolveContextPath("api")).isEqualTo("/api")
        assertThat(StartupAccessInfo.resolveContextPath("/api")).isEqualTo("/api")
    }

    @Test
    fun resolveProfilesDefaultsWhenEmpty() {
        assertThat(StartupAccessInfo.resolveProfiles(emptyArray())).isEqualTo("default")
    }

    @Test
    fun resolveProfilesJoinsActiveProfiles() {
        assertThat(StartupAccessInfo.resolveProfiles(arrayOf("test", "local")))
            .isEqualTo("test, local")
    }

    @Test
    fun formatBannerIncludesAccessLinks() {
        val banner = StartupAccessInfo.formatBanner(
            port = 8080,
            contextPath = null,
            activeProfiles = arrayOf("test"),
        )

        assertThat(banner)
            .contains("http://localhost:8080")
            .contains("http://localhost:8080/api/v1/health")
            .contains("http://localhost:8080/swagger-ui.html")
            .contains("http://localhost:8080/v3/api-docs")
            .contains(StartupAccessInfo.REPO_URL)
            .contains("test")
            .contains("8080")
    }

    @Test
    fun formatBannerUsesContextPathAndDefaultProfile() {
        val banner = StartupAccessInfo.formatBanner(
            port = null,
            contextPath = "/api",
            activeProfiles = emptyArray(),
        )

        assertThat(banner)
            .contains("http://localhost:8080/api/api/v1/health")
            .contains("default")
            .contains("8080")
    }
}
