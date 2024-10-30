package com.petcaresuite.management.application.dto

data class EmployeeFilterDTO(
    val userName: String?,
    var password: String?,
    val email: String?,
    val rol: String?,
    val name: String?,
    val country: String?,
    val enabled: Boolean?,
    var companyId: Long?,
)
