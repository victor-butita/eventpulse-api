package com.eventpulse.api.service

import com.eventpulse.api.dto.AuthResponse
import com.eventpulse.api.dto.RegisterRequest
import com.eventpulse.api.entity.Role
import com.eventpulse.api.entity.User
import com.eventpulse.api.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest {

    private lateinit var authService: AuthService

    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()

    @BeforeEach
    fun setUp() {
        authService = AuthService(userRepository, passwordEncoder)
    }

    @Test
    fun registerShouldSaveUserAndReturnSuccessMessage() {

        val request = RegisterRequest(
            email = "antony@example.com",
            password = "Password123",
            role = Role.ATTENDEE
        )

        every { userRepository.existsByEmail(request.email) } returns false
        every { passwordEncoder.encode(request.password) } returns "encodedPassword"

        val userSlot = slot<User>()

        every {
            userRepository.save(capture(userSlot))
        } answers { userSlot.captured }

        val response = authService.register(request)

        assertThat(response)
            .isEqualTo(AuthResponse("User registered successfully"))

        assertThat(userSlot.captured.email)
            .isEqualTo(request.email)

        assertThat(userSlot.captured.password)
            .isEqualTo("encodedPassword")

        assertThat(userSlot.captured.role)
            .isEqualTo(Role.ATTENDEE)

        verify(exactly = 1) {
            userRepository.save(userSlot.captured)
        }
    }

    @Test
    fun registerShouldThrowExceptionWhenEmailAlreadyExists() {

        val request = RegisterRequest(
            email = "antony@example.com",
            password = "Password123",
            role = Role.ATTENDEE
        )

        every { userRepository.existsByEmail(request.email) } returns true

        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.register(request)
        }

        assertThat(exception.message)
            .isEqualTo("Email already exists")

        verify(exactly = 0) {
            userRepository.save(ofType(User::class))
        }
    }
}