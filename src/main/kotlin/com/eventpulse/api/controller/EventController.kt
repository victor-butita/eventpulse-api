package com.eventpulse.api.controller
import org.springframework.data.domain.Page
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.dto.CreateEventRequest
import com.eventpulse.api.dto.UpdateEventRequest
import com.eventpulse.api.entity.Event
import com.eventpulse.api.service.EventService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService
) {

    @GetMapping
    fun getEvents(

        @RequestParam(defaultValue = "0")
        page: Int,

        @RequestParam(defaultValue = "10")
        size: Int,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        date: LocalDate?,

        @RequestParam(required = false)
        status: EventStatus?

    ): ResponseEntity<Page<Event>> {

        return ResponseEntity.ok(
            eventService.getEvents(
                page,
                size,
                date,
                status
            )
        )
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    fun createEvent(
        @Valid @RequestBody request: CreateEventRequest,
        authentication: Authentication
    ): ResponseEntity<Event> {

        val event = eventService.createEvent(
            request,
            authentication.name
        )

        return ResponseEntity(event, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    fun updateEvent(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateEventRequest,
        authentication: Authentication
    ): ResponseEntity<Event> {

        val event = eventService.updateEvent(
            id,
            request,
            authentication.name
        )

        return ResponseEntity.ok(event)
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ORGANIZER')")
    fun cancelEvent(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Event> {

        val event = eventService.cancelEvent(
            id,
            authentication.name
        )

        return ResponseEntity.ok(event)
    }
}