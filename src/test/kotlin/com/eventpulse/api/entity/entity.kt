package com.eventpulse.api.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventTest {

    @Test
    fun shouldCreateEventWithCorrectValues() {

        val organizer = User(
            id = 1L,
            email = "organizer@example.com",
            password = "password",
            role = Role.ORGANIZER
        )

        val date = LocalDateTime.of(2026, 8, 1, 10, 0)

        val event = Event(
            id = 1L,
            title = "Spring Boot Workshop",
            description = "Learn Spring Boot",
            organizer = organizer,
            date = date,
            location = "Nairobi",
            ticketQuota = 100
        )

        assertThat(event.id).isEqualTo(1L)
        assertThat(event.title).isEqualTo("Spring Boot Workshop")
        assertThat(event.description).isEqualTo("Learn Spring Boot")
        assertThat(event.organizer).isEqualTo(organizer)
        assertThat(event.date).isEqualTo(date)
        assertThat(event.location).isEqualTo("Nairobi")
        assertThat(event.ticketQuota).isEqualTo(100)
        assertThat(event.ticketsBooked).isEqualTo(0)
        assertThat(event.status).isEqualTo(EventStatus.OPEN)
    }

    @Test
    fun shouldUpdateEventProperties() {

        val organizer = User(
            id = 1L,
            email = "organizer@example.com",
            password = "password",
            role = Role.ORGANIZER
        )

        val event = Event(
            title = "Old Title",
            description = "Old Description",
            organizer = organizer,
            date = LocalDateTime.now(),
            location = "Kisumu",
            ticketQuota = 50
        )

        event.title = "New Title"
        event.ticketQuota = 150
        event.status = EventStatus.CLOSED

        assertThat(event.title).isEqualTo("New Title")
        assertThat(event.ticketQuota).isEqualTo(150)
        assertThat(event.status).isEqualTo(EventStatus.CLOSED)
    }
}