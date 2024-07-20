package com.petcaresuite.management.application.mapper

import com.petcaresuite.management.application.dto.PermissionDTO
import com.petcaresuite.management.domain.model.Permission
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PermissionMapper {

    fun toDomain(permissionDTO: PermissionDTO): Permission

    fun toDTO(permission: Permission): PermissionDTO

}