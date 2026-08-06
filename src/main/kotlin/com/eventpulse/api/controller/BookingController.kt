package com.eventpulse.api.controller

import com.eventpulse.api.entity.Booking
import com.eventpulse.api.service.BookingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/booking")
//@SecurityRequirement(name = "Bearer Authentication")
class BookingController(
    private val bookingService: BookingService
) {

    @PostMapping("/{id}/book")
    @PreAuthorize("hasRole('ATTENDEE')")

    fun bookEvent(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Booking> {

        val booking = bookingService.bookEvent(id, authentication.name)

        return ResponseEntity.status(HttpStatus.CREATED).body(booking)
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ATTENDEE')")
    fun cancelBooking(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Booking> {

        val booking = bookingService.cancelBooking(
            id,
            authentication.name
        )

        return ResponseEntity.ok(booking)
    }
}