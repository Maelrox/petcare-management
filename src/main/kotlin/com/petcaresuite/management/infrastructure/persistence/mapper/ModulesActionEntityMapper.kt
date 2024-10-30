package com.petcaresuite.management.infrastructure.persistence.mapper

import org.mapstruct.Mapper
import com.petcaresuite.management.domain.model.ModulesAction
import com.petcaresuite.management.infrastructure.persistence.entity.ModulesActionEntity

@Mapper(componentModel = "spring")
interface ModulesActionEntityMapper {

    fun toEntity(modulesAction: ModulesAction): ModulesActionEntity

    fun toEntity(modulesAction: List<ModulesAction>): List<ModulesActionEntity>

    fun toDomain(modulesActionEntity: ModulesActionEntity): ModulesAction

    fun toDomain(modulesActionEntity: List<ModulesActionEntity>): List<ModulesAction>

    fun toDomainMutable(modulesActionEntity: List<ModulesActionEntity>): MutableSet<ModulesAction>


}