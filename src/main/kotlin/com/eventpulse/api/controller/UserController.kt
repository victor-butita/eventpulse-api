package com.eventpulse.api.controller

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController {

    @GetMapping("/me")
    fun currentUser(authentication: Authentication): Map<String, String> {

        return mapOf(
            "email" to authentication.name,
            "message" to "JWT authentication is working!"
        )
    }
}