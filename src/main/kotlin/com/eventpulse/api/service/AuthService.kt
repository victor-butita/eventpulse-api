package com.eventpulse.api.service

import com.eventpulse.api.dto.AuthResponse
import com.eventpulse.api.dto.LoginRequest
import com.eventpulse.api.dto.RegisterRequest
import com.eventpulse.api.entity.User
import com.eventpulse.api.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    fun register(request: RegisterRequest): AuthResponse {

        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = request.role
        )

        userRepository.save(user)

        val token = jwtService.generateToken(user.email)

        return AuthResponse(
            token = token,
            message = "User registered successfully"
        )
    }

    fun login(request: LoginRequest): AuthResponse {

        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        val token = jwtService.generateToken(user.email)

        return AuthResponse(
            token = token,
            message = "Login successful"
        )
    }
}