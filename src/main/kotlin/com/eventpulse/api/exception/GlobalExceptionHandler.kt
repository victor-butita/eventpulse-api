package com.eventpulse.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @RestControllerAdvice
    class GlobalExceptionHandler {

        @ExceptionHandler(IllegalArgumentException::class)
        fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    mapOf(
                        "message" to ex.message.orEmpty()
                    )
                )
        }
    }
}