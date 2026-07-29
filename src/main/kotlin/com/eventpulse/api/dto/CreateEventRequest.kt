package com.eventpulse.api.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class CreateEventRequest(

    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Description is required")
    val description: String,

    @field:Future(message = "Event date must be in the future")
    val date: LocalDateTime,

    @field:NotBlank(message = "Location is required")
    val location: String,

    @field:Positive(message = "Ticket quota must be greater than zero")
    val ticketQuota: Int
)