package com.petcaresuite.management.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.petcaresuite.management.application.service.messages.Responses
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ModulesActionSimple(
    val id: Long?,
    @field:NotNull(message = Responses.MODULES_ACTION_NAME_REQUIRED)
    @field:Size(min = 3, max = 32, message = Responses.MODULES_ACTION_LENGTH_INVALID)
    val name: String,
    val module: ModuleNoAction?,
)