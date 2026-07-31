package com.eventpulse.api.repository

import com.eventpulse.api.entity.Event
import com.eventpulse.api.entity.EventStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface EventRepository : JpaRepository<Event, Long> {

    fun findByStatus(
        status: EventStatus,
        pageable: Pageable
    ): Page<Event>

    fun findByStatusNot(
        status: EventStatus,
        pageable: Pageable
    ): Page<Event>

    fun findByDateBetween(
        start: LocalDateTime,
        end: LocalDateTime,
        pageable: Pageable
    ): Page<Event>

    fun findByStatusAndDateBetween(
        status: EventStatus,
        start: LocalDateTime,
        end: LocalDateTime,
        pageable: Pageable
    ): Page<Event>
}