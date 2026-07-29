package com.eventpulse.api.controller

import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.entity.Event
import com.eventpulse.api.service.EventService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService
) {

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    fun createEvent(
        @Valid
        @RequestBody request: CreateEventRequest,
        authentication: Authentication
    ): ResponseEntity<Event> {

        val event = eventService.createEvent(
            request,
            authentication.name
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(event)
    }
}