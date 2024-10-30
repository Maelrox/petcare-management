package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.CompanyMapper
import com.petcaresuite.management.application.port.input.CompanyUseCase
import com.petcaresuite.management.application.port.output.CompanyPersistencePort
import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.CompanyDomainService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CompanyService(
    private val companyDomainService: CompanyDomainService,
    private val companyPersistencePort: CompanyPersistencePort,
    private val companyMapper: CompanyMapper,
    private val userService: UserService
) :
    CompanyUseCase {

    @Transactional
    override fun update(companyDTO: CompanyDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        val company = companyPersistencePort.findById(user.company!!.id)
            ?: throw IllegalArgumentException(Responses.COMPANY_IDENTIFICATION_DOESNT_EXIST.format(user.company!!.id))
        validateUpdate(companyDTO, user, company, user.company!!.id)
        val updatableCompany = setUpdatableFields(companyDTO, company)
        companyPersistencePort.save(updatableCompany)
        return ResponseDTO(Responses.COMPANY_UPDATED)
    }

    override fun get(): CompanyDTO {
        val user = userService.getCurrentUser()
        return companyMapper.toDTO(user.company!!)
    }

    private fun validateUpdate(companyDTO: CompanyDTO, user: User, company: Company, companyId: Long) {
        companyDomainService.validateUserCompanyAccess(user, companyId)
        if (company.name != companyDTO.name) {
            companyDomainService.validateName(companyDTO.name)
        }
        if (company.companyIdentification != companyDTO.companyIdentification) {
            companyDomainService.validateCompanyIdentification(companyDTO.companyIdentification)
        }
    }

    private fun setUpdatableFields(companyDTO: CompanyDTO, company: Company): Company {
        return company.copy(
            companyIdentification = companyDTO.companyIdentification,
            name = companyDTO.name,
            users =  emptyList(),
            country = companyDTO.country
        )
    }

}