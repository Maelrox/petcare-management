package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.Company

interface CompanyPersistencePort {
    fun save(company: Company): Company
    fun findCompanyByIdentification(id: String): Company?
    fun findCompanyByName(companyName: String): Company?

}