package com.petcaresuite.management.application.dto

import jakarta.validation.constraints.*

data class UserRegisterDTO(
    @field:NotNull(message = "Username is required")
    @field:Size(min = 1, max = 255, message = "Username length must be between 1 and 255 characters")
    val userName: String?,

    @field:NotNull(message = "Password is required")
    @field:Size(min = 8, max = 255, message = "Password length must be between 8 and 255 characters")
    var password: String?,

    @field:NotNull(message = "Email is required")
    @field:Email(message = "Email should be valid")
    @field:Size(min = 1, max = 255, message = "Email length must be between 1 and 255 characters")
    val email: String?,

    @field:NotEmpty(message = "At least one role must be specified")
    val roles: Set<String>?,

    @field:NotEmpty(message = "Name required")
    val name: String?,

    @field:Pattern(regexp="\\(\\d{3}\\)\\d{3}-\\d{4}", message="Phone must match (###)###-####")
    val phone: String?,

    @field:Size(min = 3, max = 255, message = "Country length must be between 3 and 255 characters")
    val country: String?,

    val enabled: Boolean?,
    val companyId: Long?
)
