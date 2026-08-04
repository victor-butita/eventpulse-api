package com.eventpulse.api.repository


import com.eventpulse.api.entity.Booking
import org.springframework.data.jpa.repository.JpaRepository

interface BookingRepository : JpaRepository<Booking, Long>{

}