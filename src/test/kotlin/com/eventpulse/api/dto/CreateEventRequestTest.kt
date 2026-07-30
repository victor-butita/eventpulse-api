package com.eventpulse.api.dto

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class CreateEventRequestTest {

    @Test
    fun shouldCreateRequestSuccessfully() {

        val request = CreateEventRequest(
            title = "Spring Boot Workshop",
            description = "Learn Spring Boot",
            date = LocalDateTime.now().plusDays(5),
            location = "Nairobi",
            ticketQuota = 100
        )

        assertEquals("Spring Boot Workshop", request.title)
        assertEquals("Learn Spring Boot", request.description)
        assertEquals("Nairobi", request.location)
        assertEquals(100, request.ticketQuota)
    }
}