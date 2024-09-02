package com.petcaresuite.management.application.mapper

import com.petcaresuite.management.application.dto.ModulesActionDTO
import org.mapstruct.Mapper
import com.petcaresuite.management.domain.model.ModulesAction

@Mapper(componentModel = "spring")
interface ModulesActionMapper {

    fun toDomain(moduleDTO: ModulesActionDTO): ModulesAction

    fun toDTO(module: ModulesAction): ModulesActionDTO

    fun toDTO(modulesAction: List<ModulesAction>): List<ModulesActionDTO>

}