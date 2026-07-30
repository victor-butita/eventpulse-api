package com.eventpulse.api.service

import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.entity.Role
import com.eventpulse.api.entity.User
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.LocalDateTime

class EventServiceTest {

    private val eventRepository = mockk<EventRepository>()
    private val userRepository = mockk<UserRepository>()

    private val eventService = EventService(
        eventRepository,
        userRepository
    )

    @Test
    fun `should create event successfully`() {

        val organizer = User(
            id = 1L,
            email = "organizer@test.com",
            password = "password",
            role = Role.ORGANIZER
        )

        val request = CreateEventRequest(
            title = "Spring Boot Workshop",
            description = "Learn Spring Boot",
            date = LocalDateTime.of(2026, 9, 1, 10, 0),
            location = "Nairobi",
            ticketQuota = 100
        )

        every { userRepository.findByEmail("organizer@test.com") } returns organizer
        every { eventRepository.save(any()) } answers { firstArg() }

        val event = eventService.createEvent(request, "organizer@test.com")

        assertEquals(request.title, event.title)
        assertEquals(request.description, event.description)
        assertEquals(request.location, event.location)
        assertEquals(request.ticketQuota, event.ticketQuota)
        assertEquals(0, event.ticketsBooked)
        assertEquals(EventStatus.OPEN, event.status)
        assertEquals(organizer, event.organizer)

        verify(exactly = 1) {
            eventRepository.save(any())
        }
    }

    @Test
    fun `should throw exception when organizer is not found`() {

        val request = CreateEventRequest(
            title = "Workshop",
            description = "Description",
            date = LocalDateTime.now(),
            location = "Kisumu",
            ticketQuota = 50
        )

        every {
            userRepository.findByEmail(any())
        } returns null

        assertThrows<UsernameNotFoundException> {
            eventService.createEvent(request, "missing@test.com")
        }

        verify(exactly = 0) {
            eventRepository.save(any())
        }
    }
}