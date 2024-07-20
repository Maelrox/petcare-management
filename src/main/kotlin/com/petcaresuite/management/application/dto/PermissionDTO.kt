package com.petcaresuite.management.application.dto

import com.petcaresuite.management.application.service.messages.Responses
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class PermissionDTO(
    val id: Long? = 0,
    @field:NotNull(message = Responses.PERMISSION_NAME_REQUIRED)
    @field:Size(min = 3, max = 32, message = Responses.PERMISSION_LENGTH_INVALID)
    val name: String,
    @field:NotNull(message = Responses.PERMISSION_ROLE_REQUIRED)
    val role: RoleDTO?,
)