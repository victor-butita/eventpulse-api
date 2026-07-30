package com.eventpulse.api.service

import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.dto.UpdateEventRequest
import com.eventpulse.api.entity.Event
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

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
            throw org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN,
                "You are not allowed to update this event."
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
            throw org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN,
                "You are not allowed to cancel this event."
            )
        }

        event.status = EventStatus.CANCELLED

        return eventRepository.save(event)
    }
}