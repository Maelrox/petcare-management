package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.dto.UserRegisterDTO
import com.petcaresuite.management.application.dto.UserUpdateDTO

interface IUserService {
    fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO

    fun update(userUpdateDTO: UserUpdateDTO): ResponseDTO

}