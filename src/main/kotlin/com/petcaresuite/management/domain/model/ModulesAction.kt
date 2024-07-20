package com.petcaresuite.management.domain.model

import java.time.Instant

data class ModulesAction(
    val id: Long?,
    val module: Module,
    val name: String,
    val createdDate: Instant?,
    val updatedDate: Instant?
)
