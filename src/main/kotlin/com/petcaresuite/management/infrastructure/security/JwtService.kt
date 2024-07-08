package com.petcaresuite.management.infrastructure.security

import com.petcaresuite.management.application.port.output.JwtPort
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
class JwtService : JwtPort {

    @Value("\${jwt.secret.key}")
    lateinit var secret: String

    override fun generateToken(username: String): Pair<String, Date> {
        val claims = HashMap<String, Any>()
        return createToken(claims, username)
    }

    private fun createToken(claims: Map<String, Any>, username: String): Pair<String, Date> {
        val expirationDate = Date(System.currentTimeMillis() + 1000 * 60 * 30)
        val token = Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .signWith(getSignKey(), Jwts.SIG.HS256)
            .compact()
        return Pair(token, expirationDate)
    }

    private fun getSignKey(): SecretKey? {
        val keyBytes =  Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    override fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
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

    override fun validateToken(token: String, userDetails: UserDetails): Boolean {
        try {
            val username = extractUsername(token)
            return username == userDetails.username && !isTokenExpired(token)
        } catch (ex: TokenExpiredException) {
            throw ex
        }
    }

}