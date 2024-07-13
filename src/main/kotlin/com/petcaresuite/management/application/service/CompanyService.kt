package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.CompanyMapper
import com.petcaresuite.management.application.port.input.CompanyUseCase
import com.petcaresuite.management.application.port.output.CompanyPersistencePort
import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.CompanyValidationService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CompanyService(
    private val companyValidationService: CompanyValidationService,
    private val companyPersistencePort: CompanyPersistencePort,
    private val userPersistencePort: UserPersistencePort,
    private val companyMapper: CompanyMapper,
    private val userService: UserService
) :
    CompanyUseCase {
    @Transactional
    override fun save(companyDTO: CompanyDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        validateCreation(companyDTO, user)
        val company = companyMapper.toDomain(companyDTO)
        val persistedCompany = companyPersistencePort.save(company)
        user.company = persistedCompany
        userPersistencePort.save(user)
        return ResponseDTO(Responses.COMPANY_CREATED)
    }

    @Transactional
    override fun update(companyDTO: CompanyDTO, companyId: Long): ResponseDTO {
        val user = userService.getCurrentUser()
        val company = companyPersistencePort.findById(companyId)
            ?: throw IllegalArgumentException(Responses.COMPANY_IDENTIFICATION_DOESNT_EXIST.format(companyId))
        validateUpdate(companyDTO, user, company, companyId)
        val updatableCompany = setUpdatableFields(companyDTO, company)
        companyPersistencePort.save(updatableCompany)
        return ResponseDTO(Responses.COMPANY_UPDATED)
    }

    private fun validateCreation(companyDTO: CompanyDTO, user: User) {
        companyValidationService.validateUserCompanyExistence(user)
        companyValidationService.validateName(companyDTO.name)
        companyValidationService.validateCompanyIdentification(companyDTO.companyIdentification)
    }

    fun validateUpdate(companyDTO: CompanyDTO, user: User, company: Company, companyId: Long) {
        companyValidationService.validateUserCompanyAccess(user, companyId)
        if (company.name != companyDTO.name) {
            companyValidationService.validateName(companyDTO.name)
        }
        if (company.companyIdentification != companyDTO.companyIdentification) {
            companyValidationService.validateCompanyIdentification(companyDTO.companyIdentification)
        }
    }

    private fun setUpdatableFields(companyDTO: CompanyDTO, company: Company): Company {
        return Company(
            companyIdentification = companyDTO.companyIdentification,
            id =  company.id,
            name = companyDTO.name,
            users =  ArrayList(),
            country = companyDTO.country
        )
    }

}