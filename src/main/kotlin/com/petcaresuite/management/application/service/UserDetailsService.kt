package com.petcaresuite.management.application.service

import com.petcaresuite.management.domain.model.CustomUserDetails
import com.petcaresuite.management.domain.repository.UserRepository
import com.petcaresuite.management.infrastructure.persistence.entity.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: Optional<User> = userRepository.getUserInfoByUsername(username)
        return user.map { user: User? ->
            CustomUserDetails(
                user!!
            )
        }.orElseThrow {
                UsernameNotFoundException(
                    "User not found: $username"
                )
            }
    }
}