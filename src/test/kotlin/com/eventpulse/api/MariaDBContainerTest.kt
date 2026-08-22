
package com.eventpulse.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MariaDBContainerTest : IntegrationTestBase() {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `MariaDB container should be running`() {
        assertTrue(mariaDB.isRunning)
    }

    @Test
    fun `should connect to MariaDB`() {

        val result = jdbcTemplate.queryForObject(
            "SELECT 1",
            Int::class.java
        )

        assertEquals(1, result)
    }
}

