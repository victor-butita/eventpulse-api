package com.eventpulse.api.service

import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.dto.UpdateEventRequest
import com.eventpulse.api.entity.Event
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
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.Optional

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
    @Test
    fun `should update event successfully`() {

        val organizer = User(
            id = 1L,
            email = "organizer@test.com",
            password = "password",
            role = Role.ORGANIZER
        )

        val event = Event(
            id = 1L,
            title = "Old Title",
            description = "Old Description",
            organizer = organizer,
            date = LocalDateTime.now().plusDays(2),
            location = "Nairobi",
            ticketQuota = 100,
            ticketsBooked = 0,
            status = EventStatus.OPEN
        )

        val request = UpdateEventRequest(
            title = "New Title",
            description = "Updated Description",
            date = LocalDateTime.now().plusDays(10),
            location = "Kisumu",
            ticketQuota = 200
        )

        every { eventRepository.findById(1L) } returns Optional.of(event)
        every { eventRepository.save(any()) } answers { firstArg() }

        val updated = eventService.updateEvent(
            1L,
            request,
            "organizer@test.com"
        )

        assertEquals("New Title", updated.title)
        assertEquals("Updated Description", updated.description)
        assertEquals("Kisumu", updated.location)
        assertEquals(200, updated.ticketQuota)

        verify(exactly = 1) {
            eventRepository.save(any())
        }
    }

    @Test
    fun `should cancel event successfully`() {

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
            status = EventStatus.OPEN
        )

        every { eventRepository.findById(1L) } returns Optional.of(event)
        every { eventRepository.save(any()) } answers { firstArg() }

        val cancelled = eventService.cancelEvent(
            1L,
            "organizer@test.com"
        )

        assertEquals(EventStatus.CANCELLED, cancelled.status)

        verify(exactly = 1) {
            eventRepository.save(any())
        }
    }

    @Test
    fun `should throw forbidden when another organizer updates event`() {

        val organizer = User(
            id = 1L,
            email = "owner@test.com",
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
            status = EventStatus.OPEN
        )

        val request = UpdateEventRequest(
            title = "New",
            description = "New",
            date = LocalDateTime.now().plusDays(3),
            location = "Kisumu",
            ticketQuota = 120
        )

        every { eventRepository.findById(1L) } returns Optional.of(event)

        assertThrows<ResponseStatusException> {
            eventService.updateEvent(
                1L,
                request,
                "another@test.com"
            )
        }
    }
}