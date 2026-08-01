package com.eventpulse.api.service

import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.dto.UpdateEventRequest
import com.eventpulse.api.entity.Event
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) {

    fun createEvent(
        request: CreateEventRequest,
        organizerEmail: String
    ): Event {

        val organizer = userRepository.findByEmail(organizerEmail)
            ?: throw UsernameNotFoundException("Organizer not found")

        val event = Event(
            title = request.title,
            description = request.description,
            organizer = organizer,
            date = request.date,
            location = request.location,
            ticketQuota = request.ticketQuota,
            ticketsBooked = 0,
            status = EventStatus.OPEN
        )

        return eventRepository.save(event)
    }

    fun updateEvent(
        eventId: Long,
        request: UpdateEventRequest,
        organizerEmail: String
    ): Event {

        val event = eventRepository.findById(eventId)
            .orElseThrow {
                NoSuchElementException("Event not found")
            }

        if (event.organizer.email != organizerEmail) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not allowed to update this event."
            )
        }

<<<<<<< HEAD
        if (request.ticketQuota < event.ticketsBooked) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ticket quota cannot be less than the number of tickets already booked (${event.ticketsBooked})."
=======
        // Prevent reducing quota below already booked tickets
        if (request.ticketQuota < event.ticketsBooked) {
            throw org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "Ticket quota cannot be less than already booked tickets."
>>>>>>> b4f375c (addtional test)
            )
        }

        event.title = request.title
        event.description = request.description
        event.date = request.date
        event.location = request.location
        event.ticketQuota = request.ticketQuota

        return eventRepository.save(event)
    }

    fun cancelEvent(
        eventId: Long,
        organizerEmail: String
    ): Event {

        val event = eventRepository.findById(eventId)
            .orElseThrow {
                NoSuchElementException("Event not found")
            }

        if (event.organizer.email != organizerEmail) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not allowed to cancel this event."
            )
        }

        event.status = EventStatus.CANCELLED

        return eventRepository.save(event)
    }

    fun getEvents(
        page: Int,
        size: Int,
        date: LocalDate?,
        status: EventStatus?
    ): Page<Event> {

        val pageable = PageRequest.of(page, size)

        return when {

            status != null && date != null ->
                eventRepository.findByStatusAndDateBetween(
                    status,
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay(),
                    pageable
                )

            status != null ->
                eventRepository.findByStatus(
                    status,
                    pageable
                )

            date != null ->
                eventRepository.findByDateBetween(
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay(),
                    pageable
                )

            else ->
                eventRepository.findByStatusNot(
                    EventStatus.CANCELLED,
                    pageable
                )
        }
    }
}