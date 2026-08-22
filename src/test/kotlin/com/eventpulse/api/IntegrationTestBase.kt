package com.eventpulse.api

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
abstract class IntegrationTestBase {

    companion object {

        @Container
        @JvmStatic
        val mariaDB = MariaDBContainer<Nothing>("mariadb:11")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(
            registry: DynamicPropertyRegistry
        ) {
            registry.add(
                "spring.datasource.url",
                mariaDB::getJdbcUrl
            )

            registry.add(
                "spring.datasource.username",
                mariaDB::getUsername
            )

            registry.add(
                "spring.datasource.password",
                mariaDB::getPassword
            )

            registry.add(
                "spring.datasource.driver-class-name"
            ) {
                "org.mariadb.jdbc.Driver"
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