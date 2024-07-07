package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.dto.UserRegisterDTO

interface IUserService {
    fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO

}