package com.petcaresuite.management.domain.mapper

import com.petcaresuite.management.application.dto.UserRegisterDTO
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface IUserDTOMapper {
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
    fun toUser(userRegisterDTO: UserRegisterDTO, roles: Set<Role>): User

}