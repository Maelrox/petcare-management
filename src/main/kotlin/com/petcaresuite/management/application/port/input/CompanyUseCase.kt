package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*
import org.springframework.web.multipart.MultipartFile

interface CompanyUseCase {

    fun update(companyDTO: CompanyDTO): ResponseDTO

    fun get(): CompanyDTO

    fun getDashboard(): CompanyDashboardDTO?

    fun uploadLogo(file: MultipartFile): CompanyDTO?

}