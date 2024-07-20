package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.infrastructure.persistence.entity.ModuleEntity
import org.mapstruct.Mapper
import com.petcaresuite.management.domain.model.Module

@Mapper(componentModel = "spring")
interface ModuleEntityMapper {
    fun toEntity(module: Module): ModuleEntity

    fun toDomain(moduleEntity: ModuleEntity): Module
}