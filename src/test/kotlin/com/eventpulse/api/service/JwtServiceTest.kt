package com.eventpulse.api.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtServiceTest {

    private lateinit var jwtService: JwtService

    // Must be at least 32 characters for HS256
    private val secret =
        "mySuperSecretKeyForJwtTesting123456789"

    // 1 hour
    private val expiration = 3600000L

    @BeforeEach
    fun setUp() {
        jwtService = JwtService(
            secret,
            expiration
        )
    }

    @Test
    fun generateTokenShouldReturnToken() {

        val token = jwtService.generateToken("antony@example.com")

        assertThat(token).isNotBlank()
    }

    @Test
    fun extractEmailShouldReturnCorrectEmail() {

        val email = "antony@example.com"

        val token = jwtService.generateToken(email)

        val extractedEmail = jwtService.extractEmail(token)

        assertThat(extractedEmail)
            .isEqualTo(email)
    }

    @Test
    fun isTokenValidShouldReturnTrueForMatchingEmail() {

        val email = "antony@example.com"

        val token = jwtService.generateToken(email)

        val isValid = jwtService.isTokenValid(
            token,
            email
        )

        assertThat(isValid).isTrue()
    }

    @Test
    fun isTokenValidShouldReturnFalseForDifferentEmail() {

        val token = jwtService.generateToken(
            "antony@example.com"
        )

        val isValid = jwtService.isTokenValid(
            token,
            "another@example.com"
        )

        assertThat(isValid).isFalse()
    }
}