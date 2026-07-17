package com.eventpulse.api.controller

import com.eventpulse.api.dto.AuthResponse
import com.eventpulse.api.dto.LoginRequest
import com.eventpulse.api.dto.RegisterRequest
import com.eventpulse.api.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): AuthResponse {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): AuthResponse {
        return authService.login(request)
    }
}