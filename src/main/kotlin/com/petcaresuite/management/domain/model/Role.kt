package com.petcaresuite.management.domain.model

data class Role(
    val id: Long,
    val name: String,
    val company: Company?,
    var permissions: MutableSet<Permission>? = null

)