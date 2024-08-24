package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.persistence.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping


@Mapper(componentModel = "spring", uses = [RoleEntityMapper::class])
interface UserEntityMapper {
    fun toEntity(userModel: User): UserEntity

    @Mapping(target = "company.users", ignore = true) // Ignore users collection in company
    fun toDomain(userEntity: UserEntity): User
}