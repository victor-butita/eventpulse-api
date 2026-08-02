package com.eventpulse.api.service

import com.eventpulse.api.entity.BookingStatus
import com.eventpulse.api.entity.Event
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.entity.Role
import com.eventpulse.api.entity.User
import com.eventpulse.api.repository.BookingRepository
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.Optional

class BookingServiceTest {

    private val bookingRepository = mockk<BookingRepository>()
    private val eventRepository = mockk<EventRepository>()
    private val userRepository = mockk<UserRepository>()

    private val bookingService = BookingService(
        bookingRepository,
        eventRepository,
        userRepository
    )

    @Test
    fun `should book event successfully`() {

        val attendee = User(
            id = 1L,
            email = "attendee@test.com",
            password = "password",
            role = Role.ATTENDEE
        )

        val organizer = User(
            id = 2L,
            email = "organizer@test.com",
            password = "password",
            role = Role.ORGANIZER
        )

        val event = Event(
            id = 1L,
            title = "Spring Boot Workshop",
            description = "Learn Spring Boot",
            organizer = organizer,
            date = LocalDateTime.now().plusDays(2),
            location = "Nairobi",
            ticketQuota = 100,
            ticketsBooked = 0,
            status = EventStatus.OPEN
        )

        every { eventRepository.findById(1L) } returns Optional.of(event)
        every { userRepository.findByEmail(attendee.email!!) } returns attendee
        every { eventRepository.save(any()) } answers { firstArg() }
        every { bookingRepository.save(any()) } answers { firstArg() }

        val booking = bookingService.bookEvent(
            1L,
            attendee.email!!
        )

        assertEquals(BookingStatus.CONFIRMED, booking.status)
        assertEquals(attendee, booking.attendee)
        assertEquals(event, booking.event)
        assertEquals(1, event.ticketsBooked)

        verify(exactly = 1) { bookingRepository.save(any()) }
        verify(exactly = 1) { eventRepository.save(any()) }
    }

    @Test
    fun `should throw exception when attendee is not found`() {

        val organizer = User(
            id = 2L,
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
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<UsernameNotFoundException> {
            bookingService.bookEvent(1L, "missing@test.com")
        }

        verify(exactly = 0) { bookingRepository.save(any()) }
    }

    @Test
    fun `should reject booking when user is not attendee`() {

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
        every { userRepository.findByEmail(any()) } returns organizer

        val exception = assertThrows<ResponseStatusException> {
            bookingService.bookEvent(1L, organizer.email!!)
        }

        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
    }

    @Test
    fun `should reject booking when event is closed`() {

        val attendee = User(
            id = 1L,
            email = "attendee@test.com",
            password = "password",
            role = Role.ATTENDEE
        )

        val organizer = User(
            id = 2L,
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

        every { eventRepository.findById(1L) } returns Optional.of(event)
        every { userRepository.findByEmail(any()) } returns attendee

        val exception = assertThrows<ResponseStatusException> {
            bookingService.bookEvent(1L, attendee.email!!)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun `should reject booking when event is sold out`() {

        val attendee = User(
            id = 1L,
            email = "attendee@test.com",
            password = "password",
            role = Role.ATTENDEE
        )

        val organizer = User(
            id = 2L,
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
            ticketsBooked = 100,
            status = EventStatus.OPEN
        )

        every { eventRepository.findById(1L) } returns Optional.of(event)
        every { userRepository.findByEmail(any()) } returns attendee

        val exception = assertThrows<ResponseStatusException> {
            bookingService.bookEvent(1L, attendee.email!!)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals("Event is sold out.", exception.reason)
    }
}