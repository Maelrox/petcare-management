package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.infrastructure.persistence.entity.CompanyEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface CompanyEntityMapper {
    @Mappings(
        Mapping(target = "users", expression = "java(java.util.Collections.emptyList())")
    )    fun toEntity(company: Company): CompanyEntity

    fun toDomain(companyEntity: CompanyEntity): Company
}