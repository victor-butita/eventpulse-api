package com.eventpulse.api.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(

    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration}")
    private val expiration: Long
) {

    private fun getSigningKey(): SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(email: String): String {

        return Jwts.builder()
            .subject(email)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact()
    }

    fun extractEmail(token: String): String =
        extractAllClaims(token).subject

    fun isTokenValid(token: String, email: String): Boolean {

        return extractEmail(token) == email &&
                !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean =
        extractExpiration(token).before(Date())

    private fun extractExpiration(token: String): Date =
        extractAllClaims(token).expiration

    private fun extractAllClaims(token: String): Claims {

        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
}