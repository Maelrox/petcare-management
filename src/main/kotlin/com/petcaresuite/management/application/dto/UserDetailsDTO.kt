package com.petcaresuite.management.application.dto

import jakarta.validation.constraints.*

data class UserDetailsDTO(
    val id: Long?,
    val roles: Set<String>?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val country: String?,
    val enabled: Boolean?,
    val companyId: Long?
)
