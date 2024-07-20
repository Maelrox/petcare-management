package com.petcaresuite.management.application.mapper

import com.petcaresuite.management.application.dto.ModuleDTO
import org.mapstruct.Mapper
import com.petcaresuite.management.domain.model.Module

@Mapper(componentModel = "spring")
interface ModuleMapper {

    fun toDomain(moduleDTO: ModuleDTO): Module

    fun toDTO(module: Module): ModuleDTO

}