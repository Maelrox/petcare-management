package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.persistence.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings


@Mapper(componentModel = "spring", uses = [RoleEntityMapper::class])
interface UserEntityMapper {
    fun toEntity(userModel: User): UserEntity

    @Mappings(
        Mapping(target = "company.users", ignore = true),
    )
    fun toDomain(userEntity: UserEntity): User
}