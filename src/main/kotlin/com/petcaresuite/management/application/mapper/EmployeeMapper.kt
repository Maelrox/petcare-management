package com.petcaresuite.management.application.mapper

import com.petcaresuite.management.application.dto.EmployeeRegisterDTO
import com.petcaresuite.management.application.dto.EmployeeFilterDTO
import com.petcaresuite.management.application.dto.UserDetailsDTO
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface EmployeeMapper {
    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "username", source = "employeeRegisterDTO.userName"),
        Mapping(target = "password", source = "employeeRegisterDTO.password"),
        Mapping(target = "email", source = "employeeRegisterDTO.email"),
        Mapping(target = "name", source = "employeeRegisterDTO.name"),
        Mapping(target = "createdDate", expression = "java(LocalDateTime.now())"),
        Mapping(target = "enabled", constant = "true"),
        Mapping(target = "roles", source = "roles"),
        Mapping(target = "country", source = "employeeRegisterDTO.country"),
        Mapping(target = "phone", source = "employeeRegisterDTO.phone"),
        Mapping(target = "lastModified", expression = "java(LocalDateTime.now())"),
        Mapping(target = "company", ignore = true)
    )
    fun toDomain(employeeRegisterDTO: EmployeeRegisterDTO, roles: Set<Role>): User

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

    fun filterToDomain(employeeDTO: EmployeeFilterDTO): User

}