package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.port.output.CompanyPersistencePort
import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.infrastructure.persistence.mapper.CompanyEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaCompanyRepository
import org.springframework.stereotype.Component

@Component
class CompanyRepositoryAdapter(
    private val companyRepository: JpaCompanyRepository,
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

}