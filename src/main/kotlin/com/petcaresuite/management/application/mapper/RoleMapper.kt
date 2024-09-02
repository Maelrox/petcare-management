package com.petcaresuite.management.application.mapper

import com.petcaresuite.management.application.dto.RoleDTO
import com.petcaresuite.management.application.dto.RoleFilterDTO
import com.petcaresuite.management.domain.model.Role
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface RoleMapper {

    fun toDomain(roleDTO: RoleDTO): Role

    fun toDomain(roleDTO: RoleFilterDTO): Role

    fun toDTO(role: Role): RoleDTO

    fun toDTO(role: List<Role>): List<RoleDTO>


}