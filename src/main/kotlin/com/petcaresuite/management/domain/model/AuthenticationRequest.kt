package com.petcaresuite.management.domain.model

data class AuthenticationRequest(
    var username: String? = null,
    var password: String? = null
)