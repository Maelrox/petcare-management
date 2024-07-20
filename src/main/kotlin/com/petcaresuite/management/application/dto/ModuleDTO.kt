package com.petcaresuite.management.application.dto

import com.petcaresuite.management.application.service.messages.Responses
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ModuleDTO(
    val id: Long?,
    @field:NotNull(message = Responses.MODULE_NAME_REQUIRED)
    @field:Size(min = 3, max = 32, message = Responses.MODULE_LENGTH_INVALID)
    val name: String,
)