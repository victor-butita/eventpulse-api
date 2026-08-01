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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import java.time.LocalDate

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

        every { userRepository.findByEmail(any()) } returns null

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
    fun `should throw when updating non existing event`() {

        every { eventRepository.findById(1L) } returns Optional.empty()

        val request = UpdateEventRequest(
            title = "New",
            description = "New",
            date = LocalDateTime.now(),
            location = "Nairobi",
            ticketQuota = 100
        )

        assertThrows<NoSuchElementException> {
            eventService.updateEvent(
                1L,
                request,
                "organizer@test.com"
            )
        }
    }


    @Test
    fun `should reject ticket quota below booked tickets`() {

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
            date = LocalDateTime.now(),
            location = "Nairobi",
            ticketQuota = 100,
            ticketsBooked = 80,
            status = EventStatus.OPEN
        )

        val request = UpdateEventRequest(
            title = "Workshop",
            description = "Updated",
            date = LocalDateTime.now(),
            location = "Nairobi",
            ticketQuota = 50
        )

        every { eventRepository.findById(1L) } returns Optional.of(event)

        val exception = assertThrows<ResponseStatusException> {
            eventService.updateEvent(
                1L,
                request,
                "organizer@test.com"
            )
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals(
            "Ticket quota cannot be less than already booked tickets.",
            exception.reason
        )
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
    fun `should throw when cancelling non existing event`() {

        every { eventRepository.findById(1L) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            eventService.cancelEvent(
                1L,
                "organizer@test.com"
            )
        }
    }

    @Test
    fun `should throw forbidden when another organizer cancels event`() {

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
            date = LocalDateTime.now(),
            location = "Nairobi",
            ticketQuota = 100,
            ticketsBooked = 0,
            status = EventStatus.OPEN
        )

        every { eventRepository.findById(1L) } returns Optional.of(event)

        assertThrows<ResponseStatusException> {
            eventService.cancelEvent(
                1L,
                "another@test.com"
            )
        }
    }

    @Test
    fun `should return events excluding cancelled by default`() {

        val pageable = PageRequest.of(0, 10)

        every {
            eventRepository.findByStatusNot(
                EventStatus.CANCELLED,
                pageable
            )
        } returns PageImpl(emptyList())

        val result = eventService.getEvents(
            0,
            10,
            null,
            null
        )

        assertTrue(result.isEmpty)

        verify {
            eventRepository.findByStatusNot(
                EventStatus.CANCELLED,
                pageable
            )
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

    @Test
<<<<<<< HEAD
    fun `should return non cancelled events by default`() {

        val pageable = PageRequest.of(0, 10)
        val events = PageImpl(emptyList<Event>())

        every {
            eventRepository.findByStatusNot(EventStatus.CANCELLED, pageable)
        } returns events

        val result = eventService.getEvents(0, 10, null, null)

        assertEquals(events, result)

        verify {
            eventRepository.findByStatusNot(EventStatus.CANCELLED, pageable)
        }
    }

    @Test
    fun `should filter events by status`() {

        val pageable = PageRequest.of(0, 10)
        val events = PageImpl(emptyList<Event>())

        every {
            eventRepository.findByStatus(EventStatus.OPEN, pageable)
        } returns events

        val result = eventService.getEvents(
=======
    fun `should return events filtered by status`() {

        val pageable = PageRequest.of(0, 10)

        every {
            eventRepository.findByStatus(
                EventStatus.OPEN,
                pageable
            )
        } returns PageImpl(emptyList())

        eventService.getEvents(
>>>>>>> b4f375c (addtional test)
            0,
            10,
            null,
            EventStatus.OPEN
        )

<<<<<<< HEAD
        assertEquals(events, result)

        verify {
            eventRepository.findByStatus(EventStatus.OPEN, pageable)
        }
    }

    @Test
    fun `should filter events by date`() {

        val pageable = PageRequest.of(0, 10)
        val date = LocalDate.of(2026, 9, 1)
        val events = PageImpl(emptyList<Event>())

        every {
            eventRepository.findByDateBetween(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                pageable
            )
        } returns events

        val result = eventService.getEvents(
            0,
            10,
            date,
            null
        )

        assertEquals(events, result)

        verify {
            eventRepository.findByDateBetween(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
=======
        verify {
            eventRepository.findByStatus(
                EventStatus.OPEN,
                pageable
            )
        }
    }
    @Test
    fun `should return events filtered by status`() {

        val pageable = PageRequest.of(0, 10)

        every {
            eventRepository.findByStatus(
                EventStatus.OPEN,
                pageable
            )
        } returns PageImpl(emptyList())

        eventService.getEvents(
            0,
            10,
            null,
            EventStatus.OPEN
        )

        verify {
            eventRepository.findByStatus(
                EventStatus.OPEN,
>>>>>>> b4f375c (addtional test)
                pageable
            )
        }
    }
<<<<<<< HEAD

    @Test
    fun `should filter events by status and date`() {

        val pageable = PageRequest.of(0, 10)
        val date = LocalDate.of(2026, 9, 1)
        val events = PageImpl(emptyList<Event>())
=======
    @Test
    fun `should return events filtered by status and date`() {

        val pageable = PageRequest.of(0, 10)

        val date = LocalDate.of(2026, 9, 1)
>>>>>>> b4f375c (addtional test)

        every {
            eventRepository.findByStatusAndDateBetween(
                EventStatus.OPEN,
<<<<<<< HEAD
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                pageable
            )
        } returns events

        val result = eventService.getEvents(
=======
                any(),
                any(),
                pageable
            )
        } returns PageImpl(emptyList())

        eventService.getEvents(
>>>>>>> b4f375c (addtional test)
            0,
            10,
            date,
            EventStatus.OPEN
        )

<<<<<<< HEAD
        assertEquals(events, result)

        verify {
            eventRepository.findByStatusAndDateBetween(
                EventStatus.OPEN,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
=======
        verify {
            eventRepository.findByStatusAndDateBetween(
                EventStatus.OPEN,
                any(),
                any(),
>>>>>>> b4f375c (addtional test)
                pageable
            )
        }
    }
}