package com.eventpulse.api.security

import com.eventpulse.api.service.CustomUserDetailsService
import com.eventpulse.api.service.JwtService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import jakarta.servlet.FilterChain

class JwtAuthenticationFilterTest {

    private val jwtService = mockk<JwtService>()
    private val userDetailsService = mockk<CustomUserDetailsService>()
    private val filterChain = mockk<FilterChain>(relaxed = true)

    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @BeforeEach
    fun setUp() {
        jwtAuthenticationFilter =
            JwtAuthenticationFilter(jwtService, userDetailsService)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun shouldContinueFilterWhenAuthorizationHeaderIsMissing() {

        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        jwtAuthenticationFilter.doFilter(
            request,
            response,
            filterChain
        )

        verify(exactly = 1) {
            filterChain.doFilter(request, response)
        }
    }

    @Test
    fun shouldAuthenticateUserWithValidToken() {

        val request = MockHttpServletRequest()
        request.addHeader(
            "Authorization",
            "Bearer valid-token"
        )

        val response = MockHttpServletResponse()

        val userDetails = User(
            "antony@example.com",
            "password",
            listOf(SimpleGrantedAuthority("ROLE_ATTENDEE"))
        )

        every {
            jwtService.extractEmail("valid-token")
        } returns "antony@example.com"

        every {
            userDetailsService.loadUserByUsername("antony@example.com")
        } returns userDetails

        every {
            jwtService.isTokenValid(
                "valid-token",
                "antony@example.com"
            )
        } returns true

        jwtAuthenticationFilter.doFilter(
            request,
            response,
            filterChain
        )

        verify {
            filterChain.doFilter(request, response)
        }
    }

    @Test
    fun shouldNotAuthenticateWhenTokenIsInvalid() {

        val request = MockHttpServletRequest()
        request.addHeader(
            "Authorization",
            "Bearer invalid-token"
        )

        val response = MockHttpServletResponse()

        val userDetails = User(
            "antony@example.com",
            "password",
            listOf(SimpleGrantedAuthority("ROLE_ATTENDEE"))
        )

        every {
            jwtService.extractEmail("invalid-token")
        } returns "antony@example.com"

        every {
            userDetailsService.loadUserByUsername("antony@example.com")
        } returns userDetails

        every {
            jwtService.isTokenValid(
                "invalid-token",
                "antony@example.com"
            )
        } returns false

        jwtAuthenticationFilter.doFilter(
            request,
            response,
            filterChain
        )

        verify {
            filterChain.doFilter(request, response)
        }
    }
}