package com.petcaresuite.management.infrastructure.persistence.mapper

import org.mapstruct.Mapper
import com.petcaresuite.management.domain.model.ModulesAction
import com.petcaresuite.management.infrastructure.persistence.entity.ModulesActionEntity
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", uses = [ModuleEntityMapper::class])
interface ModulesActionEntityMapper {

    @Mapping(target = "module", source = "module")
    fun toEntity(modulesAction: ModulesAction): ModulesActionEntity

    @Mapping(target = "module", source = "module")
    fun toDomain(modulesActionEntity: ModulesActionEntity): ModulesAction

}