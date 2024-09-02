package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.ModulesActionMapper
import com.petcaresuite.management.application.port.input.PermissionModulesActionUseCase
import com.petcaresuite.management.application.port.output.ModulesActionPersistencePort
import com.petcaresuite.management.application.port.output.PermissionPersistencePort
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.ModulesAction
import com.petcaresuite.management.domain.model.Permission
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.service.ModulesActionValidationService
import com.petcaresuite.management.domain.service.PermissionDomainService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PermissionModulesActionService(
    private val permissionDomainService: PermissionDomainService,
    private val permissionPersistencePort: PermissionPersistencePort,
    private val moduleActionPersistencePort : ModulesActionPersistencePort,
    private val modulesActionMapper: ModulesActionMapper,
    private val rolePersistencePort: RolePersistencePort,
    private val userService: UserService,
    private val modulesActionValidationService: ModulesActionValidationService
) :
    PermissionModulesActionUseCase {

    @Transactional
    override fun save(permissionModuleActionDTO: PermissionModuleActionDTO): ResponseDTO {
        val permission = permissionPersistencePort.findById(permissionModuleActionDTO.permission.id!!)
        val user = userService.getCurrentUser()
        val roles = rolePersistencePort.findAllByCompanyId(user.company?.id!!)
        val moduleAction = modulesActionMapper.toDomain(permissionModuleActionDTO.moduleAction)
        validateCreation(permission, moduleAction, roles)
        permission.modulesAction?.add(moduleAction)
        permissionPersistencePort.save(permission)
        return ResponseDTO(Responses.PERMISSION_MODULES_ACTION_CREATED)
    }

    override fun getAllByPermissionIdAndModuleId(permissionId: Long, moduleId: Long): List<ModulesActionDTO> {
        val modulesAction =moduleActionPersistencePort.getByPermissionIdAndModuleId(permissionId, moduleId)
        return modulesActionMapper.toDTO(modulesAction)
    }

    private fun validateCreation(permission: Permission, moduleAction: ModulesAction, roles: List<Role>) {
        modulesActionValidationService.validateCompanyRoles(roles)
        permissionDomainService.validatePermissionAccess(roles, permission.id!!)
        modulesActionValidationService.validateModulesActionId(moduleAction.id!!)
    }

}