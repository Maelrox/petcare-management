package com.petcaresuite.management.infrastructure.security

import com.petcaresuite.management.application.security.ILoginAttemptService
import com.petcaresuite.management.domain.valueobject.CustomUserDetails
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.repository.IUserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserDetailsService(
    private val userRepository: IUserRepository,
    private val loginAttemptAdapter: LoginAttemptAdapter
) :
    UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        if (loginAttemptAdapter.isBlocked()) {
            throw IllegalAccessException("Too many attempts wait 24 hours");
        }
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