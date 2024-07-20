package com.petcaresuite.management.domain.model

data class Permission(
    val id: Int? = null,
    val name: String,
    var modulesAction: MutableSet<ModulesAction>? = null
)