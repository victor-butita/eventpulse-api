package com.eventpulse.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventPulseApplication

fun main(args: Array<String>) {
    runApplication<EventPulseApplication>(*args)
}
