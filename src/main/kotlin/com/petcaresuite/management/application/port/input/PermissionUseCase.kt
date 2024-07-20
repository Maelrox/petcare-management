package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*

interface PermissionUseCase {
    
    fun save(permissionDTO: PermissionDTO): ResponseDTO

    fun update(permissionDTO: PermissionDTO): ResponseDTO

}