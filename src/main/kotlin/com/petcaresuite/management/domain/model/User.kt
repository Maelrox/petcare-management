    package com.petcaresuite.management.domain.model

    import java.time.LocalDateTime

    data class User(
        val id: Long = 0,
        val username: String,
        val password: String?,
        val email: String,
        val name: String?,
        val phone: String?,
        val country: String?,
        val enabled: Boolean,
        val lastModified: LocalDateTime?,
        val createdDate: LocalDateTime,
        val company: Company?,
        val roles: Set<Role>
    )