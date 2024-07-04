package com.petcaresuite.management.domain.model

data class Vet(
    val id: Long?,
    val identification: String,
    val name: String,
    val phone: String?,
    val specialization: String?,
    val identificationType: IdentificationType
)