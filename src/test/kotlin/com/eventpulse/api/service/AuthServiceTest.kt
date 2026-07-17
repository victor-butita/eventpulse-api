package com.eventpulse.api.service

import com.eventpulse.api.dto.AuthResponse
import com.eventpulse.api.dto.LoginRequest
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
    private val jwtService = mockk<JwtService>()

    @BeforeEach
    fun setUp() {
        authService = AuthService(
            userRepository,
            passwordEncoder,
            jwtService
        )
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
        every { jwtService.generateToken(request.email) } returns "mock-jwt-token"

        val userSlot = slot<User>()

        every {
            userRepository.save(capture(userSlot))
        } answers { userSlot.captured }

        val response = authService.register(request)

        assertThat(response.message)
            .isEqualTo("User registered successfully")

        assertThat(response.token)
            .isEqualTo("mock-jwt-token")

        assertThat(userSlot.captured.email)
            .isEqualTo(request.email)

        assertThat(userSlot.captured.password)
            .isEqualTo("encodedPassword")

        assertThat(userSlot.captured.role)
            .isEqualTo(Role.ATTENDEE)

        verify(exactly = 1) {
            userRepository.save(any())
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
            userRepository.save(any())
        }
    }

    @Test
    fun loginShouldReturnTokenWhenCredentialsAreValid() {

        val request = LoginRequest(
            email = "antony@example.com",
            password = "Password123"
        )

        val user = User(
            email = request.email,
            password = "encodedPassword",
            role = Role.ATTENDEE
        )

        every { userRepository.findByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.password) } returns true
        every { jwtService.generateToken(user.email) } returns "mock-jwt-token"

        val response = authService.login(request)

        assertThat(response.message)
            .isEqualTo("Login successful")

        assertThat(response.token)
            .isEqualTo("mock-jwt-token")
    }

    @Test
    fun loginShouldThrowExceptionWhenEmailDoesNotExist() {

        val request = LoginRequest(
            email = "missing@example.com",
            password = "Password123"
        )

        every { userRepository.findByEmail(request.email) } returns null

        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.login(request)
        }

        assertThat(exception.message)
            .isEqualTo("Invalid email or password")
    }

    @Test
    fun loginShouldThrowExceptionWhenPasswordIsIncorrect() {

        val request = LoginRequest(
            email = "antony@example.com",
            password = "WrongPassword"
        )

        val user = User(
            email = request.email,
            password = "encodedPassword",
            role = Role.ATTENDEE
        )

        every { userRepository.findByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.password) } returns false

        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.login(request)
        }

        assertThat(exception.message)
            .isEqualTo("Invalid email or password")
    }
}