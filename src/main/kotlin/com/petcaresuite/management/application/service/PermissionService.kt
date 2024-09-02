package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.CompanyMapper
import com.petcaresuite.management.application.mapper.ModulesActionMapper
import com.petcaresuite.management.application.mapper.PermissionMapper
import com.petcaresuite.management.application.port.input.PermissionUseCase
import com.petcaresuite.management.application.port.output.ModulePersistencePort
import com.petcaresuite.management.application.port.output.ModulesActionPersistencePort
import com.petcaresuite.management.application.port.output.PermissionPersistencePort
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.service.PermissionDomainService

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional

@Service
class PermissionService(
    private val permissionDomainService: PermissionDomainService,
    private val permissionPersistencePort: PermissionPersistencePort,
    private val rolePersistencePort: RolePersistencePort,
    private val modulesActionPersistencePort: ModulesActionPersistencePort,
    private val modulePersistencePort: ModulePersistencePort,
    private val permissionMapper: PermissionMapper,
    private val modulesActionMapper: ModulesActionMapper,
    private val companyMapper: CompanyMapper,
    private val userService: UserService,
    private val moduleService: ModuleService,
) : PermissionUseCase {

    @Transactional
    override fun saveRoles(permissionRolesDTO: PermissionRolesDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        val permission = permissionPersistencePort.findById(permissionRolesDTO.id!!)
        val validRoles = rolePersistencePort.findAllByCompanyId(user.company!!.id)
        val rolesToUpdate = permissionRolesDTO.roles!!.mapNotNull { roleDTO ->
            validRoles.find { it.id == roleDTO.id }
        }.toSet()
        permissionDomainService.updatePermissionRoles(permission, rolesToUpdate, validRoles, user)
        rolePersistencePort.saveAll(validRoles)
        var permissionsToRemoveFromRole =
            permissionDomainService.filterPermissionsToRemove(permission, rolesToUpdate, validRoles)
        if (permissionsToRemoveFromRole.isNotEmpty()) {
            permissionPersistencePort.deleteRemovedRoles(permission.id!!, permissionsToRemoveFromRole)
        }
        return ResponseDTO(Responses.PERMISSION_UPDATED.format(permission.name))
    }

    @Transactional
    override fun saveModules(permissionModulesDTO: PermissionModulesDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        val permission = permissionPersistencePort.findById(permissionModulesDTO.id)
        val modulesActionIds = permissionModulesDTO.modulesAction!!.mapNotNull { it.id }
        val validActions = modulesActionPersistencePort.getAllByIdIn(modulesActionIds)
        var currentActions = permission.modulesAction

        val actionsToUpdate =
            permissionModulesDTO.modulesAction!!.map { modulesActionMapper.toDomain(it) }.toMutableSet()

        val updatedActions =
            permissionDomainService.updatePermissionModules(currentActions, actionsToUpdate, validActions, user)
        permission.modulesAction!!.addAll(updatedActions)
        permissionPersistencePort.save(permission)
        permissionPersistencePort.deleteRemovedModules(permission.id!!, modulesActionIds)
        return ResponseDTO(Responses.PERMISSION_UPDATED.format(permission.name))
    }

    override fun save(permissionDTO: PermissionDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        val permissions = permissionPersistencePort.findAllByCompanyId(user.company!!.id)
        permissionDomainService.validateCreation(permissionDTO, permissions)
        val companyDTO = companyMapper.toDTO(user.company!!)
        permissionDTO.company = companyDTO
        var permission = permissionMapper.toDomain(permissionDTO)
        permissionPersistencePort.save(permission)!!
        return ResponseDTO(Responses.PERMISSION_CREATED.format(permissionDTO.name))
    }

    override fun update(permissionDTO: PermissionDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        val permissions = permissionPersistencePort.findAllByCompanyId(user.company!!.id)
        permissionDomainService.validateCreation(permissionDTO, permissions)
        val companyDTO = companyMapper.toDTO(user.company!!)
        permissionDTO.company = companyDTO
        var permission = permissionMapper.toDomain(permissionDTO)
        permissionPersistencePort.update(permission)!!
        return ResponseDTO(Responses.PERMISSION_UPDATED.format(permissionDTO.name))
    }

    override fun getAllByFilterPaginated(filterDTO: PermissionDTO, pageable: Pageable): Page<PermissionDTO> {
        val user = userService.getCurrentUser()
        val companyId = user.company!!.id
        val filter = permissionMapper.toDomain(filterDTO)

        return permissionPersistencePort.findAllByFilterPaginated(filter, pageable, companyId)
            .map { permissionMapper.toDTO(it) }
    }

    @Transactional
    override fun delete(id: Long): ResponseDTO? {
        val user = userService.getCurrentUser()
        val permission = permissionPersistencePort.findById(id)
        permissionDomainService.validateDeletion(user, permission)
        permissionPersistencePort.delete(id)
        return ResponseDTO(Responses.PERMISSION_DELETED)
    }

    override fun hasPermission(module: String, action: String): Boolean {
        val user = userService.getCurrentUser()
        if (user.roles.isEmpty()) return false

        val moduleEntity = modulePersistencePort.findByName(module) ?: return false
        val actionId = moduleEntity.modulesActionEntities!!.find { it.name == action }?.id ?: return false

        return user.roles.any { role ->
            role.permissions?.flatMap { it.modulesAction ?: emptyList() }?.any { it.id == actionId } == true
        }
    }
}