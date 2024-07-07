package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.AuthenticationRequestDTO
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO

interface IAuthenticationService {

    fun authenticate(request: AuthenticationRequestDTO): AuthenticationResponseDTO

}