package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*

interface PermissionModulesActionUseCase {
    
    fun save(permissionModuleActionDTO: PermissionModuleActionDTO): ResponseDTO

}