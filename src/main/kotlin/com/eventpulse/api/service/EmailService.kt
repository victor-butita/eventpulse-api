package com.eventpulse.api.service

import com.eventpulse.api.entity.Booking
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {

    @Async
    fun sendBookingConfirmation(booking: Booking) {

        val message = SimpleMailMessage()

        message.setTo(booking.attendee.email)
        message.subject = "EventPulse - Booking Confirmation"

        message.text = """
            Booking Confirmed!

            Your booking has been successfully confirmed.

            Event: ${booking.event.title}
            Date: ${booking.event.date}
            Booking Reference: ${booking.id}

            Thank you for using EventPulse.
        """.trimIndent()

        mailSender.send(message)
    }
}