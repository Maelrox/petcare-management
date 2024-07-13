package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*

interface CompanyUseCase {
    fun save(companyDTO: CompanyDTO): ResponseDTO
    fun update(companyDTO: CompanyDTO, companyId: Long): ResponseDTO
}