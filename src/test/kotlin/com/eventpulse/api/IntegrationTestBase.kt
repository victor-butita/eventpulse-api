package com.eventpulse.api

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@ActiveProfiles("test")
abstract class IntegrationTestBase {

    companion object {

        @Container
        @JvmStatic
        val mysql = MySQLContainer<Nothing>("mysql:8.0")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(
            registry: DynamicPropertyRegistry
        ) {
            registry.add(
                "spring.datasource.url",
                mysql::getJdbcUrl
            )

            registry.add(
                "spring.datasource.username",
                mysql::getUsername
            )

            registry.add(
                "spring.datasource.password",
                mysql::getPassword
            )

            registry.add(
                "spring.datasource.driver-class-name"
            ) {
                "com.mysql.cj.jdbc.Driver"
            }

            registry.add(
                "spring.jpa.database-platform"
            ) {
                "org.hibernate.dialect.MySQLDialect"
            }

            registry.add(
                "spring.jpa.hibernate.ddl-auto"
            ) {
                "validate"
            }

            registry.add(
                "spring.liquibase.enabled"
            ) {
                "true"
            }

            registry.add(
                "spring.liquibase.change-log"
            ) {
                "classpath:db/changelog/db.changelog-master.yaml"
            }
        }
    }
}
