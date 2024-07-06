package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.persistence.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface IUserMapper {
    fun toEntity(userModel: User): UserEntity

    @Mapping(target = "password", ignore = true) // TODO: Check this Optionally ignore sensitive fields
    fun toModel(userEntity: UserEntity): User
}