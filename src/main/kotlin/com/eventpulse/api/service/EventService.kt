package com.eventpulse.api.service

import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.entity.Event
<<<<<<< Updated upstream
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
=======
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            ?: throw UsernameNotFoundException("Organizer not found")
=======
            ?: throw IllegalArgumentException("Organizer not found")
>>>>>>> Stashed changes

        val event = Event(
            title = request.title,
            description = request.description,
            organizer = organizer,
            date = request.date,
            location = request.location,
<<<<<<< Updated upstream
            ticketQuota = request.ticketQuota
=======
            ticketQuota = request.ticketQuota,
            ticketsBooked = 0,
            status = EventStatus.OPEN
>>>>>>> Stashed changes
        )

        return eventRepository.save(event)
    }
}