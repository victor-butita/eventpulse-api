package com.eventpulse.api.service

import com.eventpulse.api.entity.Event
import com.eventpulse.api.entity.EventStatus
import com.eventpulse.api.entity.Role
import com.eventpulse.api.entity.User
import com.eventpulse.api.repository.BookingRepository
import com.eventpulse.api.repository.EventRepository
import com.eventpulse.api.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class BookingServiceConcurrencyTest {

    @Autowired
    lateinit var bookingService: BookingService

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var bookingRepository: BookingRepository

    @Test
    fun `should not oversell tickets under concurrent bookings`() {

        val organizer = userRepository.save(
            User(
                email = "organizer@test.com",
                password = "password",
                role = Role.ORGANIZER
            )
        )

        val attendee = userRepository.save(
            User(
                email = "attendee@test.com",
                password = "password",
                role = Role.ATTENDEE
            )
        )

        val event = eventRepository.save(
            Event(
                title = "Spring Boot",
                description = "Workshop",
                organizer = organizer,
                date = LocalDateTime.now().plusDays(1),
                location = "Nairobi",
                ticketQuota = 5,
                ticketsBooked = 0,
                status = EventStatus.OPEN
            )
        )

        val threads = 20
        val executor = Executors.newFixedThreadPool(threads)

        // Makes every thread wait until they are all ready
        val startLatch = CountDownLatch(1)

        // Lets the main thread know when everyone has finished
        val finishLatch = CountDownLatch(threads)

        repeat(threads) {
            executor.submit {
                try {
                    // Wait until every thread is ready
                    startLatch.await()

                    bookingService.bookEvent(
                        event.id!!,
                        attendee.email!!
                    )

                } catch (_: Exception) {
                    // Expected when tickets are exhausted
                } finally {
                    finishLatch.countDown()
                }
            }
        }

        // Release all threads at the same time
        startLatch.countDown()

        // Wait for every thread to finish
        finishLatch.await()

        val updatedEvent = eventRepository.findById(event.id!!).get()
        val bookings = bookingRepository.findAll()

        assertTrue(updatedEvent.ticketsBooked <= updatedEvent.ticketQuota)
        assertTrue(bookings.size <= updatedEvent.ticketQuota)

        executor.shutdown()
    }
}