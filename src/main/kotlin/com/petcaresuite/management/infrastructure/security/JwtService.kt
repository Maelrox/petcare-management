package com.petcaresuite.management.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtService {

    @Value("\${jwt.secret.key}")
    lateinit var secret: String

    fun generateToken(username: String): String {
        val claims = HashMap<String, Any>()
        return createToken(claims, username)
    }

    private fun createToken(claims: Map<String, Any>, username: String): String {
        return Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 30))
            .signWith(getSignKey(), Jwts.SIG.HS256)
            .compact()
    }

    private fun getSignKey(): SecretKey? {
        val keyBytes =  Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    interface ClaimExtractor<T> {
        fun extract(claims: Claims): T
    }

    private inline fun <reified T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        val secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (ex: ExpiredJwtException) {
            throw TokenExpiredException("JWT token has expired")
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        try {
            val username = extractUsername(token)
            return username == userDetails.username && !isTokenExpired(token)
        } catch (ex: TokenExpiredException) {
            throw ex
        }
    }

}