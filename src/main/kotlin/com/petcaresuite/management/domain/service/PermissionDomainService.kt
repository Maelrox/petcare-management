package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.dto.PermissionDTO
import com.petcaresuite.management.application.service.UserService
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.*
import org.springframework.stereotype.Service

@Service
class PermissionDomainService(
    private val userService: UserService
) {

    fun validateRelation(permission: Permission, roles: List<Role>, company: Company) {
        validateCompany(permission, company, roles)
        validateDuplicated(permission, roles)
    }

    fun validateCreation(permissionDTO: PermissionDTO, permissions: Set<Permission>) {
        validateNameDuplicated(permissionDTO.name!!, permissions)
    }

    /**
     * Remove and add roles to the permission model
     * validate that the current roles belong to the company
     */
    fun updatePermissionRoles(
        permission: Permission,
        rolesToUpdate: Set<Role>,
        validRoles: List<Role>,
        user: User
    ): Set<Role> {
        val updatedRoles = mutableSetOf<Role>()
        rolesToUpdate.forEach { role ->
            if (!validRoles.contains(role)) {
                throw IllegalArgumentException("Role with id ${role.id} not found")
            }
            updatedRoles.add(role)
            if (!role.permissions!!.contains(permission)) {
                role.permissions!!.add(permission)
            }
        }
        validateRelation(permission, updatedRoles.toList(), user.company!!)
        return updatedRoles
    }

    fun updatePermissionModules(
        currentActionModules: MutableSet<ModulesAction>?,
        moduleActionsToUpdate: MutableSet<ModulesAction>,
        validModuleActions: List<ModulesAction>,
        user: User
    ): Set<ModulesAction> {
        val updatedActions = mutableSetOf<ModulesAction>()
        moduleActionsToUpdate.forEach { moduleAction ->
            if (!validModuleActions.contains(moduleAction)) {
                throw IllegalArgumentException("Module action with id ${moduleAction.id} not found")
            }
            if (!currentActionModules?.contains(moduleAction)!!) {
                updatedActions.add(moduleAction)
            }
        }
        return updatedActions.toSet()
    }

    fun filterPermissionsToRemove(
        permission: Permission,
        updatedRoles: Set<Role>,
        validRoles: List<Role>
    ): List<Long> {
        return validRoles.filter { role ->
            role.permissions?.contains(permission) == true && !updatedRoles.contains(role)
        }.mapNotNull { it.id }
    }

    private fun validateNameDuplicated(permissionName: String, permissions: Set<Permission>?) {
        permissions?.let {
            if (it.any { permission -> permission.name == permissionName }) {
                throw IllegalArgumentException(Responses.PERMISSION_ALREADY_EXISTS)
            }
        }
    }

    private fun validateUserCompany(companyId: Long) {
        val user = userService.getCurrentUser()
        if (user.company?.id != companyId) {
            throw IllegalArgumentException(Responses.USER_IS_NOT_MEMBER_OF_COMPANY)
        }
    }

    fun validatePermissionAccess(roles: List<Role>, permissionId: Long) {
        val foundPermission = roles.flatMap { role ->
            role.permissions?.filter { permission ->
                permission.id == permissionId
            }.orEmpty()
        }.any()
        if (!foundPermission) {
            throw IllegalStateException(Responses.PERMISSION_ALREADY_EXISTS_FOR_THE_ROLE)
        }
    }

    private fun validatePermissionAlreadyExist(id: Long?, permissions: MutableSet<Permission>?) {
        permissions?.let {
            if (it.any { permission -> permission.id == id }) {
                throw IllegalArgumentException(Responses.PERMISSION_ALREADY_EXISTS_FOR_THE_ROLE)
            }
        }
    }

    private fun validateDuplicated(permissionToUpdate: Permission, roles: List<Role>) {
        roles?.let {
            if (it.any { permission -> permission.id == permissionToUpdate.id }) {
                throw IllegalArgumentException(Responses.PERMISSION_ALREADY_EXISTS_FOR_THE_ROLE)
            }
        }
    }

    private fun validateCompany(permission: Permission, company: Company, roles: List<Role>) {
        if (permission == null) {
            throw IllegalArgumentException(Responses.PERMISSION_NOT_FOUND)
        }
        if (company.id != permission.company!!.id) {
            throw IllegalArgumentException(Responses.PERMISSION_NOT_PART_OF_THE_COMPANY)
        }
    }

    fun validateDeletion(user: User, permission: Permission) {
        if (user.company!!.id != permission.company!!.id) {
            throw IllegalArgumentException(Responses.PERMISSION_NOT_PART_OF_THE_COMPANY)
        }
    }

}