package com.petcaresuite.management.application.dto

data class AuthenticationRequestDTO(
    var userName: String? = null,
    var password: String? = null
)