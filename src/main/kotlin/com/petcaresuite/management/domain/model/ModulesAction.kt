package com.petcaresuite.management.domain.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ModulesAction(
    val id: Long?,
    val module: Module,
    val name: String,
    val createdDate: Instant?,
    val updatedDate: Instant?
)
