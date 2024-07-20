package com.petcaresuite.management.domain.model

data class Module(
    val id: Long?,
    val name: String,
    val modulesActions: List<ModulesAction>?
)
