package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.infrastructure.persistence.entity.RoleEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface RoleEntityMapper {
    fun toEntity(roleModel: Role): RoleEntity

    fun toDomain(roleEntity: RoleEntity): Role
}