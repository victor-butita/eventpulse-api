package com.eventpulse.api.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {

        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                mapOf(
                    "status" to HttpStatus.BAD_REQUEST.value(),
                    "error" to "Validation failed",
                    "message" to "Request validation failed",
                    "errors" to errors,
                    "path" to request.requestURI
                )
            )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFoundException(
        ex: NoSuchElementException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                mapOf(
                    "status" to HttpStatus.NOT_FOUND.value(),
                    "error" to "Not Found",
                    "message" to (ex.message ?: "Resource not found"),
                    "path" to request.requestURI
                )
            )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                mapOf(
                    "status" to HttpStatus.BAD_REQUEST.value(),
                    "error" to "Bad Request",
                    "message" to (ex.message ?: "Invalid request"),
                    "path" to request.requestURI
                )
            )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                mapOf(
                    "status" to HttpStatus.FORBIDDEN.value(),
                    "error" to "Forbidden",
                    "message" to "You do not have permission to access this resource",
                    "path" to request.requestURI
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                mapOf(
                    "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error" to "Internal Server Error",
                    "message" to "An unexpected error occurred",
                    "path" to request.requestURI
                )
            )
    }
}