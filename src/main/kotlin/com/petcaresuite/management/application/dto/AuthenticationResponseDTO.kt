package com.petcaresuite.management.application.dto

import java.util.*

data class AuthenticationResponseDTO(
    var message: String?,
    var token: String? = null,
    val expirationDate: Date,
    var userDetails: UserDetailsDTO,
) {



}