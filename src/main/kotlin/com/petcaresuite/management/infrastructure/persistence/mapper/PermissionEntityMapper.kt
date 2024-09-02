package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.Permission
import com.petcaresuite.management.infrastructure.persistence.entity.PermissionEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [ModulesActionEntityMapper::class])
interface PermissionEntityMapper {

    @Mappings(
        Mapping(target = "company.users", ignore = true),
    )
    fun toEntity(permission: Permission): PermissionEntity

    @Mappings(
        Mapping(target = "company.users", ignore = true),
    )
    fun toDomain(permissionEntity: PermissionEntity): Permission

    fun toDomain(permissionEntity: Set<PermissionEntity>): Set<Permission>
}