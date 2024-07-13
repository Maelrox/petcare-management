package com.petcaresuite.management.application.dto

import java.util.*

data class AuthenticationResponseDTO(
    var token: String? = null,
    val expirationDate: Date,
    var userDetailsDTO: UserDetailsDTO,
    var message: String?
    //TODO: Permissions by module
) {



}