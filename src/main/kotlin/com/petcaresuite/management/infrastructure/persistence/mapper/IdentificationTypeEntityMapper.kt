package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.IdentificationType
import com.petcaresuite.management.infrastructure.persistence.entity.IdentificationTypeEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface IdentificationTypeEntityMapper {

    fun toEntity(identificationTypes: List<IdentificationType>): List<IdentificationTypeEntity>

    fun toDomain(identificationTypesEntity: List<IdentificationTypeEntity>): List<IdentificationType>

    fun toEntity(identificationType: IdentificationType): IdentificationTypeEntity

    fun toDomain(identificationTypeEntity: IdentificationTypeEntity): IdentificationType

}