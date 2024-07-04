package com.petcaresuite.management.domain.model

import com.petcaresuite.management.infrastructure.persistence.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(user: User) : UserDetails {
    private val name: String = user.username
    private val password: String = user.password
    private val authorities: List<GrantedAuthority> = user.roles.split(",")
        .asSequence()
        .map { role -> SimpleGrantedAuthority(role.trim()) }
        .toList()

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return name
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}