package com.petcaresuite.management.application.dto

import com.petcaresuite.management.application.service.messages.Responses
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RoleDTO(
    val id: Long? = 0,
    @field:NotNull(message = Responses.ROLE_NAME_REQUIRED)
    @field:Size(min = 3, max = 32, message = Responses.ROLE_LENGTH_INVALID)
    val name: String?,
    @field:NotNull(message = Responses.ROLE_COMPANY_REQUIRED)
    val company: CompanyDTO?,
)