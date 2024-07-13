package com.petcaresuite.management.domain.model

data class Company(
    val id: Long,
    val name: String,
    val country: String?,
    val companyIdentification: String,
    val users: List<User>?
)