package com.eventpulse.api.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdminControllerTest {

    private val adminController = AdminController()

    @Test
    fun organizerOnlyShouldReturnWelcomeMessage() {

        val response = adminController.organizerOnly()

        assertEquals(
            "Welcome Organizer!",
            response["message"]
        )
    }
}