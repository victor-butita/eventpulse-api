package com.eventpulse.api.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bookings")
class Booking(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    val attendee: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BookingStatus = BookingStatus.CONFIRMED,

    @Column(name = "booked_at", nullable = false)
    val bookedAt: LocalDateTime = LocalDateTime.now()
)