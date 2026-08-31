package com.eventpulse.api.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment

class JdbcUrlNormalizerTest {

    @Test
    fun prefixesRailwayMysqlScheme() {
        val raw = "mysql://root:secret@mysql.railway.internal:3306/railway"
        assertThat(JdbcUrlNormalizer.normalize(raw))
            .isEqualTo(
                "jdbc:mysql://root:secret@mysql.railway.internal:3306/railway" +
                    "?allowPublicKeyRetrieval=true",
            )
    }

    @Test
    fun convertsMariadbSchemeToJdbcMysql() {
        assertThat(JdbcUrlNormalizer.normalize("mariadb://db:3306/eventpulse"))
            .isEqualTo("jdbc:mysql://db:3306/eventpulse?allowPublicKeyRetrieval=true")
    }

    @Test
    fun leavesJdbcMysqlUrlsIntact() {
        val url = "jdbc:mysql://localhost:3306/eventpulse?allowPublicKeyRetrieval=true"
        assertThat(JdbcUrlNormalizer.normalize(url)).isEqualTo(url)
    }

    @Test
    fun appendsPublicKeyFlagWhenMissing() {
        assertThat(JdbcUrlNormalizer.normalize("jdbc:mysql://localhost:3306/eventpulse"))
            .isEqualTo("jdbc:mysql://localhost:3306/eventpulse?allowPublicKeyRetrieval=true")
    }

    @Test
    fun appendsPublicKeyFlagWhenUrlAlreadyHasQuery() {
        assertThat(
            JdbcUrlNormalizer.normalize("jdbc:mysql://localhost:3306/eventpulse?useUnicode=true"),
        ).isEqualTo(
            "jdbc:mysql://localhost:3306/eventpulse?useUnicode=true&allowPublicKeyRetrieval=true",
        )
    }

    @Test
    fun returnsBlankAndNullUnchanged() {
        assertThat(JdbcUrlNormalizer.normalize(null)).isNull()
        assertThat(JdbcUrlNormalizer.normalize("")).isEqualTo("")
        assertThat(JdbcUrlNormalizer.normalize("   ")).isEqualTo("   ")
    }

    @Test
    fun postProcessorRewritesSpringDatasourceUrl() {
        val environment = StandardEnvironment()
        environment.propertySources.addFirst(
            MapPropertySource(
                "test",
                mapOf(
                    "SPRING_DATASOURCE_URL" to
                        "mysql://root:secret@mysql.railway.internal:3306/railway",
                ),
            ),
        )

        JdbcUrlEnvironmentPostProcessor().postProcessEnvironment(
            environment,
            SpringApplication(),
        )

        assertThat(environment.getProperty("spring.datasource.url"))
            .startsWith("jdbc:mysql://")
            .contains("allowPublicKeyRetrieval=true")
    }

    @Test
    fun postProcessorNoopsWhenUrlMissing() {
        val environment = StandardEnvironment()

        JdbcUrlEnvironmentPostProcessor().postProcessEnvironment(
            environment,
            SpringApplication(),
        )

        assertThat(
            environment.propertySources.contains(
                JdbcUrlEnvironmentPostProcessor.PROPERTY_SOURCE_NAME,
            ),
        ).isFalse()
    }
}
