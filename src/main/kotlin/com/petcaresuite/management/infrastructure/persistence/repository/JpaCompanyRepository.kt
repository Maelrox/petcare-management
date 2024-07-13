package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.infrastructure.persistence.entity.CompanyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JpaCompanyRepository : JpaRepository<CompanyEntity, Long> {
    fun save(company: CompanyEntity): CompanyEntity
    fun findByName(companyName: String): Optional<CompanyEntity>
    fun findByCompanyIdentification(id: String): Optional<CompanyEntity>

}