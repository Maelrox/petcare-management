package com.petcaresuite.management.domain.model

data class RegisterDTO(
    val userName: String,
    val password: String,
    val email: String,
    val roles: String

)