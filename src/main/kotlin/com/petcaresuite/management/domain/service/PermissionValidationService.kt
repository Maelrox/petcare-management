package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.service.UserService
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Permission
import com.petcaresuite.management.domain.model.Role
import org.springframework.stereotype.Service

@Service
class PermissionValidationService(
    private val userService: UserService
) {

    fun validateNameDuplicated(permissionName: String, permissions: Set<Permission>?) {
        permissions?.let {
            if (it.any { permission -> permission.name == permissionName }) {
                throw IllegalArgumentException(Responses.PERMISSION_ALREADY_EXISTS)
            }
        }
    }

    fun validateUserCompany(companyId: Long) {
        val user = userService.getCurrentUser()
        if (user.company?.id != companyId) {
            throw IllegalArgumentException(Responses.USER_IS_NOT_MEMBER_OF_COMPANY)
        }
    }

    fun validatePermissionAccess(roles: Set<Role>, permissionId: Int) {
        val foundPermission = roles.flatMap { role ->
            role.permissions?.filter { permission ->
                permission.id == permissionId
            }.orEmpty()
        }.any()
        if (!foundPermission) {
            throw IllegalStateException(Responses.PERMISSION_NOT_PART_OF_THE_COMPANY)
        }
    }


}