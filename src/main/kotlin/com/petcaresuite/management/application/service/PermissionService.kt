package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.PermissionMapper
import com.petcaresuite.management.application.port.input.PermissionUseCase
import com.petcaresuite.management.application.port.output.PermissionPersistencePort
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.service.PermissionValidationService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PermissionService(
    private val permissionValidationService: PermissionValidationService,
    private val permissionPersistencePort: PermissionPersistencePort,
    private val rolePersistencePort: RolePersistencePort,
    private val permissionMapper: PermissionMapper
) :
    PermissionUseCase {

    @Transactional
    override fun save(permissionDTO: PermissionDTO): ResponseDTO {
        val role = rolePersistencePort.findById(permissionDTO.role?.id!!)
        validateCreation(permissionDTO, role)
        var permission = permissionMapper.toDomain(permissionDTO)
        permission = permissionPersistencePort.save(permission)!!
        role.permissions?.add(permission)
        rolePersistencePort.save(role)
        return ResponseDTO(Responses.PERMISSION_CREATED.format(permissionDTO.name))
    }

    private fun validateCreation(permissionDTO: PermissionDTO, role: Role) {
        permissionValidationService.validateUserCompany(permissionDTO.role?.company?.id!!)
        permissionValidationService.validateNameDuplicated(permissionDTO.name, role.permissions)
    }

    override fun update(permissionDTO: PermissionDTO): ResponseDTO {
        TODO("Not yet implemented")
    }


}