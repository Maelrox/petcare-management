package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.dto.ConsultationTrendDTO
import com.petcaresuite.management.application.dto.InventorySalesDTO
import com.petcaresuite.management.application.dto.MonthlyConsultationCount
import com.petcaresuite.management.application.dto.MonthlyOwnerCount
import com.petcaresuite.management.application.port.output.CompanyPersistencePort
import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.infrastructure.persistence.mapper.CompanyEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaCompanyRepository
import com.petcaresuite.management.infrastructure.persistence.repository.JpaDashboardRepository
import org.springframework.stereotype.Component

@Component
class CompanyRepositoryAdapter(
    private val companyRepository: JpaCompanyRepository,
    private val dashboardRepository: JpaDashboardRepository,
    private val companyMapper: CompanyEntityMapper
) : CompanyPersistencePort {
    override fun save(company: Company): Company {
        val companyEntity = companyMapper.toEntity(company)
        companyRepository.save(companyEntity)
        return companyMapper.toDomain(companyEntity)
    }

    override fun findCompanyByIdentification(id: String): Company? {
        val companyEntityOptional = companyRepository.findByCompanyIdentification(id)
        return companyEntityOptional.orElse(null)?.let { companyMapper.toDomain(it) }
    }

    override fun findCompanyByName(companyName: String): Company? {
        val companyEntityOptional = companyRepository.findByName(companyName)
        return companyEntityOptional.orElse(null)?.let { companyMapper.toDomain(it) }
    }

    override fun findById(id: Long): Company? {
        val companyEntityOptional = companyRepository.findById(id)
        return companyEntityOptional.orElse(null)?.let { companyMapper.toDomain(it) }
    }

    override fun getMonthlyOwners(companyId: Long): List<MonthlyOwnerCount> {
        return dashboardRepository.getMonthlyOwners(companyId)
    }

    override fun getCurrentMonthOwners(companyId: Long): Int {
        return dashboardRepository.getCurrentMonthOwners(companyId)
    }

    override fun getPreviousMonthOwners(companyId: Long): Int {
        return dashboardRepository.getPreviousMonthOwners(companyId)
    }

    override fun getMonthlyConsultations(companyId: Long): List<MonthlyConsultationCount> {
        return dashboardRepository.getMonthlyConsultations(companyId)
    }

    override fun getAllConsultationTrends(companyId: Long): ConsultationTrendDTO {
        return dashboardRepository.getAllConsultationTrends(companyId)
    }

    override fun getInventorySales(companyId: Long): InventorySalesDTO {
        return dashboardRepository.getInventorySales(companyId)
    }

}