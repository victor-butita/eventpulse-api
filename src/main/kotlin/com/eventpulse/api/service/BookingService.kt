package com.eventpulse.api.service

import com.eventpulse.api.entity.Booking
import com.eventpulse.api.entity.BookingStatus
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.entity.Role
import com.eventpulse.api.repository.BookingRepository
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) {

    /**
     * Books an event for an attendee.
     *
     * Uses optimistic locking (@Version) on Event to prevent overselling.
     * If two users attempt to book the last available ticket simultaneously,
     * only one transaction succeeds. The other fails with an
     * ObjectOptimisticLockingFailureException.
     */
    @Transactional
    fun bookEvent(
        eventId: Long,
        attendeeEmail: String
    ): Booking {

        try {

            val event = eventRepository.findById(eventId)
                .orElseThrow {
                    IllegalArgumentException("Event not found.")
                }

            val attendee = userRepository.findByEmail(attendeeEmail)
                ?: throw UsernameNotFoundException("Attendee not found.")

            // Only attendees are allowed to book events.
            if (attendee.role != Role.ATTENDEE) {
                throw ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only attendees can book events."
                )
            }

            // Event must be open.
            if (event.status != EventStatus.OPEN) {
                throw IllegalArgumentException("Event is not open for booking.")
            }

            // No more tickets available.
            if (event.ticketsBooked >= event.ticketQuota) {
                throw IllegalArgumentException("Event is sold out.")
            }

            // Reserve one ticket.
            event.ticketsBooked++
            eventRepository.save(event)

            // Create booking.
            val booking = Booking(
                event = event,
                attendee = attendee,
                status = BookingStatus.CONFIRMED
            )

            return bookingRepository.save(booking)

        } catch (ex: ObjectOptimisticLockingFailureException) {

            // Another user booked the last ticket first.
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Someone else booked the last available ticket. Please try again."
            )
        }
    }

    @Transactional
    fun cancelBooking(
        bookingId: Long,
        attendeeEmail: String
    ): Booking {

        val booking = bookingRepository.findById(bookingId)
            .orElseThrow {
                IllegalArgumentException("Booking not found.")
            }

        if (booking.attendee.email != attendeeEmail) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You can only cancel your own booking."
            )
        }

        if (booking.status == BookingStatus.CANCELLED) {
            throw IllegalArgumentException("Booking is already cancelled.")
        }

        booking.status = BookingStatus.CANCELLED

        val event = eventRepository.findById(booking.event.id!!)
            .orElseThrow {
                IllegalArgumentException("Event not found.")
            }

        if (event.ticketsBooked > 0) {
            event.ticketsBooked--
        }

        eventRepository.save(event)
        println("After save: ${booking.event.ticketsBooked}")

        return bookingRepository.save(booking)
    }
}