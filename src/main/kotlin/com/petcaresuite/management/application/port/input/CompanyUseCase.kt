package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*

interface CompanyUseCase {

    fun update(companyDTO: CompanyDTO): ResponseDTO

    fun get(): CompanyDTO

    fun getDashboard(): CompanyDashboardDTO?
}