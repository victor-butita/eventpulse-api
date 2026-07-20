package com.eventpulse.api.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class AdminController {

    @GetMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    fun organizerOnly(): Map<String, String> {

        return mapOf(
            "message" to "Welcome Organizer!"
        )
    }
}