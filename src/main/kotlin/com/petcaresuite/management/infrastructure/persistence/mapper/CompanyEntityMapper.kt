package com.petcaresuite.management.infrastructure.persistence.mapper

import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.infrastructure.persistence.entity.CompanyEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface CompanyEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "companyIdentification", source = "companyIdentification")
    @Mapping(target = "users", expression = "java(java.util.Collections.emptyList())")
    fun toEntity(company: Company): CompanyEntity

    @Mapping(target = "users", expression = "java(java.util.Collections.emptyList())")
    fun toDomain(companyEntity: CompanyEntity): Company
}