package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*

interface IUserService {
    fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO

    fun update(userUpdateDTO: UserUpdateDTO): ResponseDTO
    abstract fun getByToken(token: String): UserDetailsDTO

}