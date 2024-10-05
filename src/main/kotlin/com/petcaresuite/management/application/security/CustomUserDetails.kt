package com.petcaresuite.management.application.security

import com.petcaresuite.management.domain.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(val user: User) : UserDetails {

    private val id: Long = user.id
    private val username: String = user.username!!
    private val password: String = user.password ?: throw IllegalArgumentException("Password cannot be null")
    private val authorities: Collection<GrantedAuthority> = user.roles
        .map { role -> SimpleGrantedAuthority(role.name) }
        .toList()

    private val enabled: Boolean = user.enabled

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        // Add custom logic here if you want to determine account expiration based on your `User` model
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        // Add custom logic here if you want to determine if the account is locked based on your `User` model
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        // Add custom logic here if you want to determine if the credentials are expired based on your `User` model
        return true
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    fun getUserId(): Long {
        return id
    }

}