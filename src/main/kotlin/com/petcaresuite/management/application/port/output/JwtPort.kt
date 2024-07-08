package com.petcaresuite.management.application.port.output

import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface JwtPort {
    fun generateToken(username: String): Pair<String, Date>
    fun extractUsername(token: String): String
    fun validateToken(token: String, userDetails: UserDetails): Boolean
}