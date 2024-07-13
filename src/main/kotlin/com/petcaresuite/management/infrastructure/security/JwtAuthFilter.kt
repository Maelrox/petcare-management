package com.petcaresuite.management.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.petcaresuite.management.application.dto.ErrorResponseDTO
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.LocalDateTime

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class, TokenExpiredException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val authHeader: String? = request.getHeader("Authorization")
            var token: String?
            if (authHeader == null || !authHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response)
                return
            }
            token = authHeader.substring(7)
            var username = jwtService.extractUsername(token)
            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(username)
                if (jwtService.validateToken(token, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
            filterChain.doFilter(request, response)
        } catch (ex: TokenExpiredException) {
            val errorResponse = ErrorResponseDTO(
                timestamp = LocalDateTime.now(),
                status = HttpStatus.UNAUTHORIZED.value(),
                error = HttpStatus.UNAUTHORIZED.reasonPhrase,
                message = ex.message ?: "Token Expired",
                path = request.requestURI
            )

            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        }
    }
}