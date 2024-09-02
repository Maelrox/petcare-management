package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.port.output.ModulePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import org.springframework.stereotype.Service

@Service
class ModuleDomainService(private val modulePersistencePort: ModulePersistencePort) {

    fun validateNameDuplicated(name: String) {
        modulePersistencePort.existsByName(name).takeIf { it }?.let {
            throw IllegalArgumentException(Responses.MODULE_ALREADY_EXIST)
        }
    }

}