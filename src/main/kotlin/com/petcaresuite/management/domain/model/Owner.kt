package com.petcaresuite.management.domain.model

data class Owner(
    val id: Long?,
    val identification: String?,
    val name: String,
    val address: String?,
    val phone: String?,
    val identificationType: IdentificationType
)