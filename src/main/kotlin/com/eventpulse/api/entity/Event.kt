package com.eventpulse.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Version
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "events")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var description: String,

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    var organizer: User,

    @Column(nullable = false)
    var date: LocalDateTime,

    @Column(nullable = false)
    var location: String,

    @Column(name = "ticket_quota", nullable = false)
    var ticketQuota: Int,

    @Column(name = "tickets_booked", nullable = false)
    var ticketsBooked: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EventStatus = EventStatus.OPEN,

    @Version
    @Column(nullable = false)
    var version: Long = 0
)