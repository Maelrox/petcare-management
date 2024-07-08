package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*

interface UserUseCase {
    fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO

    fun update(userUpdateDTO: UserUpdateDTO): ResponseDTO
    fun getByToken(token: String): UserDetailsDTO

}