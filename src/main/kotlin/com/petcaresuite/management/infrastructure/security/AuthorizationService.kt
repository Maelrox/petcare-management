package com.petcaresuite.management.infrastructure.security

import com.petcaresuite.management.application.port.input.AuthorizationUseCase
import com.petcaresuite.management.application.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    private val userService: UserService,
) : AuthorizationUseCase {

    override fun isAuthorized(operation: String, module: String): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val user = userService.getByUserName(username)
        return user.roles.any { it.name == "SYSADMIN" }
        //val requiredPermission = "${targetClass.simpleName}.${method.name}"
        //val userPermissions = permissionRepository.findByUserId(user.id)
        //return userPermissions.any { it.name == requiredPermission }
    }
}