package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import org.springframework.stereotype.Service

@Service
class RoleDomainService(private val rolePersistencePort : RolePersistencePort) {

    fun validateNameDuplicated(name: String, id: Long) {
        rolePersistencePort.existsByNameAndCompanyId(name, id).takeIf { it }?.let {
            throw IllegalArgumentException(Responses.ROLE_ALREADY_EXIST)
        }
    }

    fun validateDeletion(role: Role, user: User) {
        if (role.company!!.id != user.company!!.id) {
            throw IllegalArgumentException(Responses.ROLE_NOT_PART_OF_THE_COMPANY)
        }
    }

}