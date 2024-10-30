package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.*
import com.thoughtworks.xstream.XStreamer.getDefaultPermissions
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

    fun getDefaultRole(company: Company, allModuleActions: MutableSet<ModulesAction>?, defaultPermissions: MutableSet<Permission>) : Role {
        return Role(
            id = null,
            name = "ADMIN",
            company = company,
            permissions = defaultPermissions,
        )
    }

    fun getDefaultPermission(company: Company, allModuleActions: MutableSet<ModulesAction>?) : Permission {
        val systemManagerPermission = Permission(
            id = null,
            name = "System Manager",
            modulesAction = allModuleActions,
            company = company
        )
        return systemManagerPermission
    }

}