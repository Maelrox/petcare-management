package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.port.output.CompanyPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.User
import org.springframework.stereotype.Service

@Service
class CompanyValidationService(
    private val companyPersistencePort: CompanyPersistencePort,
) {

    fun validateName(companyName: String) {
        companyPersistencePort.findCompanyByName(companyName)?.let {
            throw IllegalArgumentException(Responses.COMPANY_NAME_ALREADY_EXIST.format(companyName))
        }
    }

    fun validateCompanyIdentification(id: String) {
        companyPersistencePort.findCompanyByIdentification(id)?.let {
            throw IllegalArgumentException(Responses.COMPANY_IDENTIFICATION_ALREADY_EXIST.format(id))
        }
    }

    fun validateUserCompanyExistence(user: User) {
        if (user?.company != null) {
            throw IllegalArgumentException(Responses.USER_IS_MEMBER_OF_ANOTHER_COMPANY)
        }
    }

    fun validateUserCompanyAccess(user: User, companyIdentification: String) {
        if (companyIdentification == user.company?.companyIdentification) {
            throw IllegalAccessException(Responses.USER_IS_NOT_MEMBER_OF_COMPANY)
        }
    }

}