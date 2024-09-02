package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.port.output.ModulePersistencePort
import com.petcaresuite.management.application.port.output.ModulesActionPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Role
import org.springframework.stereotype.Service

@Service
class ModulesActionValidationService(
    private val modulesActionPersistencePort: ModulesActionPersistencePort,
    private val modulePersistencePort: ModulePersistencePort
) {

    fun validateNameDuplicated(name: String, moduleId: Long) {
        modulesActionPersistencePort.existsByNameAndModuleId(name, moduleId).takeIf { it }?.let {
            throw IllegalArgumentException(Responses.MODULES_ACTION_ALREADY_EXIST)
        }
    }

    fun validateModuleId(id: Long) {
        modulePersistencePort.existsById(id).takeUnless { it }?.let {
            throw IllegalArgumentException(Responses.MODULE_NOT_FOUND)
        }
    }

    fun validateModulesActionId(id: Long) {
        modulesActionPersistencePort.existsById(id).takeUnless { it }?.let {
            throw IllegalArgumentException(Responses.MODULE_NOT_FOUND)
        }
    }

    fun validateCompanyRoles(roles: List<Role>) {
        if (roles.isEmpty()) {
            throw IllegalArgumentException(Responses.COMPANY_WITHOUT_ROLES)
        }
    }

}