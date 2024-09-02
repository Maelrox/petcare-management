package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PermissionUseCase {
    
    fun save(permissionDTO: PermissionDTO): ResponseDTO

    fun saveRoles(permissionRolesDTO: PermissionRolesDTO): ResponseDTO

    fun saveModules(permissionModulesDTO: PermissionModulesDTO): ResponseDTO

    fun update(permissionDTO: PermissionDTO): ResponseDTO

    fun getAllByFilterPaginated(filterDTO: PermissionDTO, pageable: Pageable): Page<PermissionDTO>

    fun delete(id: Long): ResponseDTO?

    fun hasPermission(module: String, action: String): Boolean

}