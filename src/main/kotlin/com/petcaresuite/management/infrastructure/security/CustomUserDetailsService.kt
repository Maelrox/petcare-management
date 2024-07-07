package com.petcaresuite.management.infrastructure.security

import com.petcaresuite.management.domain.valueobject.CustomUserDetails
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.repository.IUserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomUserDetailsService(private val userRepository: IUserRepository) : UserDetailsService {

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