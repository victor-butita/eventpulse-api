package com.eventpulse.api.controller

import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.entity.Event
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.entity.Role
import com.eventpulse.api.entity.User
import com.eventpulse.api.service.EventService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import com.eventpulse.api.dto.UpdateEventRequest
import kotlin.test.assertEquals

class EventControllerTest {

    private val eventService = mockk<EventService>()
    private val controller = EventController(eventService)

    @Test
    fun createEventShouldReturnCreatedResponse() {

        val request = CreateEventRequest(
            title = "Workshop",
            description = "Spring Boot",
            date = LocalDateTime.now().plusDays(1),
            location = "Nairobi",
            ticketQuota = 100
        )

        val organizer = User(
            id = 1L,
            email = "organizer@test.com",
            password = "password",
            role = Role.ORGANIZER
        )

        val event = Event(
            id = 1L,
            title = request.title,
            description = request.description,
            organizer = organizer,
            date = request.date,
            location = request.location,
            ticketQuota = request.ticketQuota,
            ticketsBooked = 0,
            status = EventStatus.OPEN
        )

        val authentication = mockk<org.springframework.security.core.Authentication>()
        every { authentication.name } returns "organizer@test.com"

        every {
            eventService.createEvent(request, "organizer@test.com")
        } returns event

        val response = controller.createEvent(request, authentication)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(event, response.body)
    }
    @Test

    fun updateEventShouldReturnOkResponse() {

        val request = UpdateEventRequest(
            title = "Updated Event",
            description = "Updated Description",
            date = LocalDateTime.now().plusDays(2),
            location = "Kisumu",
            ticketQuota = 150
        )

        val organizer = User(
            id = 1L,
            email = "organizer@test.com",
            password = "password",
            role = Role.ORGANIZER
        )

        val event = Event(
            id = 1L,
            title = request.title!!,
            description = request.description!!,
            organizer = organizer,
            date = request.date!!,
            location = request.location!!,
            ticketQuota = request.ticketQuota!!,
            ticketsBooked = 0,
            status = EventStatus.OPEN
        )

        val authentication = mockk<org.springframework.security.core.Authentication>()
        every { authentication.name } returns "organizer@test.com"

        every {
            eventService.updateEvent(
                1L,
                request,
                "organizer@test.com"
            )
        } returns event

        val response = controller.updateEvent(
            1L,
            request,
            authentication
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(event, response.body)
    }

    @Test
    fun cancelEventShouldReturnOkResponse() {

        val organizer = User(
            id = 1L,
            email = "organizer@test.com",
            password = "password",
            role = Role.ORGANIZER
        )

        val event = Event(
            id = 1L,
            title = "Workshop",
            description = "Spring",
            organizer = organizer,
            date = LocalDateTime.now().plusDays(2),
            location = "Nairobi",
            ticketQuota = 100,
            ticketsBooked = 0,
            status = EventStatus.CANCELLED
        )

        val authentication = mockk<org.springframework.security.core.Authentication>()
        every { authentication.name } returns "organizer@test.com"

        every {
            eventService.cancelEvent(
                1L,
                "organizer@test.com"
            )
        } returns event

        val response = controller.cancelEvent(
            1L,
            authentication
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(EventStatus.CANCELLED, response.body!!.status)
    }
}