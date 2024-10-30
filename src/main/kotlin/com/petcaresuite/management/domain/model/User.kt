    package com.petcaresuite.management.domain.model

    import java.time.LocalDateTime

    data class User(
        val id: Long = 0,
        val username: String?,
        var password: String?,
        var email: String,
        var name: String?,
        var phone: String?,
        var country: String?,
        var enabled: Boolean,
        var lastModified: LocalDateTime?,
        val createdDate: LocalDateTime?,
        var company: Company?,
        var roles: Set<Role>,
        var companyId: Long?
    )