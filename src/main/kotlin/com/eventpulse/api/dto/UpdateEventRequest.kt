package com.eventpulse.api.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class UpdateEventRequest(

    val title: String?,

    val description: String?,

    @field:Future(message = "Event date must be in the future")
    val date: LocalDateTime?,

    val location: String?,

    @field:Positive(message = "Ticket quota must be greater than zero")
    val ticketQuota: Int?
)