package com.eventpulse.api.exception

import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `should return 404 when resource is not found`() {
        val request = MockHttpServletRequest()
        request.requestURI = "/api/events/999"

        val exception = NoSuchElementException("Event not found")

        val response = handler.handleNotFoundException(
            exception,
            request
        )

        assertEquals(404, response.statusCode.value())

        val body = response.body!!

        assertEquals(404, body["status"])
        assertEquals("Not Found", body["error"])
        assertEquals("Event not found", body["message"])
        assertEquals("/api/events/999", body["path"])
    }

    @Test
    fun `should return 400 when illegal argument is provided`() {
        val request = MockHttpServletRequest()
        request.requestURI = "/api/events"

        val exception = IllegalArgumentException(
            "Ticket quota must be greater than zero"
        )

        val response = handler.handleIllegalArgument(
            exception,
            request
        )

        assertEquals(400, response.statusCode.value())

        val body = response.body!!

        assertEquals(400, body["status"])
        assertEquals("Bad Request", body["error"])
        assertEquals(
            "Ticket quota must be greater than zero",
            body["message"]
        )
        assertEquals("/api/events", body["path"])
    }

    @Test
    fun `should return 500 when unexpected exception occurs`() {
        val request = MockHttpServletRequest()
        request.requestURI = "/api/events"

        val exception = RuntimeException("Database connection failed")

        val response = handler.handleUnexpectedException(
            exception,
            request
        )

        assertEquals(500, response.statusCode.value())

        val body = response.body!!

        assertEquals(500, body["status"])
        assertEquals("Internal Server Error", body["error"])
        assertEquals(
            "An unexpected error occurred",
            body["message"]
        )
        assertEquals("/api/events", body["path"])
    }
}