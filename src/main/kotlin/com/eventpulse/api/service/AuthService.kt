package com.eventpulse.api.service

import com.eventpulse.api.dto.AuthResponse
import com.eventpulse.api.dto.RegisterRequest
import com.eventpulse.api.entity.User
import com.eventpulse.api.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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

        return AuthResponse("User registered successfully")
    }
}