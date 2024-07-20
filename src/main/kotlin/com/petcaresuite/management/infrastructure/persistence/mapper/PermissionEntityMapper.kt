package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.Permission
import com.petcaresuite.management.infrastructure.persistence.entity.PermissionEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", uses = [ModulesActionEntityMapper::class])
interface PermissionEntityMapper {

    fun toEntity(permission: Permission): PermissionEntity

    fun toDomain(permissionEntity: PermissionEntity): Permission
}