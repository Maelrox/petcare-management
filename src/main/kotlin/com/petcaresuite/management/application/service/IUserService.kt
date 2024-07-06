package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.dto.UserRegisterDTO

interface IUserService {
    fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO

}