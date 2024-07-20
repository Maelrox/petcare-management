package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import org.springframework.stereotype.Service

@Service
class RoleValidationService(private val rolePersistencePort : RolePersistencePort) {

    fun validateNameDuplicated(name: String, id: Long) {
        rolePersistencePort.existsByNameAndCompanyId(name, id).takeIf { it }?.let {
            throw IllegalArgumentException(Responses.ROLE_ALREADY_EXIST)
        }
    }

}