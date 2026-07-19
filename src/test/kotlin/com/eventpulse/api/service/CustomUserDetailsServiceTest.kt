package com.eventpulse.api.service

import com.eventpulse.api.entity.Role
import com.eventpulse.api.entity.User
import com.eventpulse.api.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UsernameNotFoundException

class CustomUserDetailsServiceTest {

    private lateinit var customUserDetailsService: CustomUserDetailsService

    private val userRepository = mockk<UserRepository>()

    @BeforeEach
    fun setUp() {
        customUserDetailsService = CustomUserDetailsService(userRepository)
    }

    @Test
    fun shouldLoadUserByUsername() {

        val user = User(
            email = "antony@example.com",
            password = "encodedPassword",
            role = Role.ATTENDEE
        )

        every {
            userRepository.findByEmail("antony@example.com")
        } returns user

        val result =
            customUserDetailsService.loadUserByUsername("antony@example.com")

        assertEquals("antony@example.com", result.username)
        assertEquals("encodedPassword", result.password)
        assertEquals(1, result.authorities.size)
        assertTrue(
            result.authorities.any {
                it.authority == "ROLE_ATTENDEE"
            }
        )
    }

    @Test
    fun shouldThrowExceptionWhenUserDoesNotExist() {

        every {
            userRepository.findByEmail("missing@example.com")
        } returns null

        assertThrows(UsernameNotFoundException::class.java) {
            customUserDetailsService.loadUserByUsername("missing@example.com")
        }
    }
}