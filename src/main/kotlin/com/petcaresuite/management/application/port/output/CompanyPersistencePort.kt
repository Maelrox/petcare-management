package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.application.dto.ConsultationTrendDTO
import com.petcaresuite.management.application.dto.InventorySalesDTO
import com.petcaresuite.management.application.dto.MonthlyConsultationCount
import com.petcaresuite.management.application.dto.MonthlyOwnerCount
import com.petcaresuite.management.domain.model.Company

interface CompanyPersistencePort {
    fun save(company: Company): Company

    fun findCompanyByIdentification(id: String): Company?

    fun findCompanyByName(companyName: String): Company?

    fun findById(id: Long): Company?

    fun getMonthlyOwners(companyId: Long): List<MonthlyOwnerCount>

    fun getCurrentMonthOwners(companyId: Long): Int

    fun getPreviousMonthOwners(companyId: Long): Int

    fun getMonthlyConsultations(companyId: Long): List<MonthlyConsultationCount>

    fun getAllConsultationTrends(companyId: Long): ConsultationTrendDTO

    fun getInventorySales(companyId: Long): InventorySalesDTO

}