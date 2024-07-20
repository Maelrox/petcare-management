package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*

interface RoleUseCase {
    
    fun save(roleDTO: RoleDTO): ResponseDTO

    fun update(roleDTO: RoleDTO): ResponseDTO

}