package com.eventpulse.api.service

import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.entity.Event
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
            ticketQuota = request.ticketQuota
        )

        return eventRepository.save(event)
    }
}