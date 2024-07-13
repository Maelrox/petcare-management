package com.petcaresuite.management.application.mapper

import com.petcaresuite.management.application.dto.CompanyDTO
import com.petcaresuite.management.domain.model.Company
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface CompanyMapper {
    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "users", ignore = true),
        )
    fun toDomain(companyDTO: CompanyDTO): Company

    fun toDTO(company: Company): CompanyDTO

}