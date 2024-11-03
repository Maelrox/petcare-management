package com.petcaresuite.management.application.mapper

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.persistence.entity.ModulesActionEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.Named
import org.mapstruct.Qualifier

@Mapper(componentModel = "spring")
interface UserMapper {
    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "username", source = "userRegisterDTO.userName"),
        Mapping(target = "password", source = "userRegisterDTO.password"),
        Mapping(target = "email", source = "userRegisterDTO.email"),
        Mapping(target = "name", source = "userRegisterDTO.name"),
        Mapping(target = "createdDate", expression = "java(LocalDateTime.now())"),
        Mapping(target = "enabled", constant = "true"),
        Mapping(target = "roles", source = "roles"),
        Mapping(target = "country", source = "userRegisterDTO.country"),
        Mapping(target = "phone", source = "userRegisterDTO.phone"),
        Mapping(target = "lastModified", expression = "java(LocalDateTime.now())"),
        Mapping(target = "company", ignore = true)
    )
    fun toDomain(userRegisterDTO: UserRegisterDTO, roles: Set<Role>): User

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "email", source = "user.email"),
        Mapping(target = "name", source = "user.name"),
        Mapping(target = "enabled", source = "user.enabled"),
        Mapping(target = "roles", source = "user.roles"),
        Mapping(target = "country", source = "user.country"),
        Mapping(target = "phone", source = "user.phone"),
        Mapping(target = "companyId", source = "user.company.id")
    )
    fun toDTO(user: User): UserDetailsDTO

    fun toDomain(userRegisterDTO: UserDetailsDTO): User

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "roles", ignore = true),
        Mapping(target = "moduleActions", ignore = true),
        Mapping(source = "user.email", target = "email"),
        Mapping(source = "user.name", target = "name"),
        Mapping(source = "user.enabled", target = "enabled"),
        Mapping(target = "actions", source= "actions"),
        Mapping(source = "user.country", target = "country"),
        Mapping(source = "user.phone", target = "phone"),
        Mapping(source = "user.company.id", target = "companyId"),
        Mapping(source = "user.username", target = "username")
    )
    fun toLoginDTO(user: User, actions: List<ModulesActionEntity>): UserDetailsDTO

}