package com.eventpulse.api.service

import com.eventpulse.api.entity.Booking
import com.eventpulse.api.entity.BookingStatus
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.repository.BookingRepository
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) {

    fun bookEvent(
        eventId: Long,
        attendeeEmail: String
    ): Booking {

        val event = eventRepository.findById(eventId)
            .orElseThrow {
                NoSuchElementException("Event not found")
            }

        val attendee = userRepository.findByEmail(attendeeEmail)
            ?: throw UsernameNotFoundException("Attendee not found")

        if (attendee.role.name != "ATTENDEE") {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Only attendees can book events."
            )
        }

        if (event.status != EventStatus.OPEN) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Event is not open for booking."
            )
        }

        if (event.ticketsBooked >= event.ticketQuota) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Event is sold out."
            )
        }

        event.ticketsBooked += 1
        eventRepository.save(event)

        val booking = Booking(
            event = event,
            attendee = attendee,
            status = BookingStatus.CONFIRMED
        )

        return bookingRepository.save(booking)
    }
}