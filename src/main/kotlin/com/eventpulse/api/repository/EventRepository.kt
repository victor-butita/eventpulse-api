package com.eventpulse.api.repository

import com.eventpulse.api.entity.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<Event, Long>