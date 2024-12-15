package com.petcaresuite.management.application.mapper

import com.petcaresuite.management.application.dto.IdentificationTypeDTO
import com.petcaresuite.management.domain.model.IdentificationType
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface IdentificationTypeMapper {

    fun toDomain(identificationTypesDTO: List<IdentificationTypeDTO>): List<IdentificationType>

    fun toDTO(identificationTypes: List<IdentificationType>): List<IdentificationTypeDTO>

    fun toDomain(identificationTypeDTO: IdentificationTypeDTO): IdentificationType

    fun toDTO(identificationType: IdentificationType): IdentificationTypeDTO

}
