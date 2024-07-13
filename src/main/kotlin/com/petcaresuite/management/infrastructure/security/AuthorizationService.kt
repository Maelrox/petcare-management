package com.petcaresuite.management.infrastructure.security

import com.petcaresuite.management.application.port.input.AuthorizationUseCase
import com.petcaresuite.management.application.service.UserService
import com.petcaresuite.management.domain.model.RoleType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.lang.reflect.Method

@Service
class AuthorizationService(
    private val userService: UserService,
) : AuthorizationUseCase {

    override fun isAuthorized(method: Method, targetClass: Class<*>): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val user = userService.getByUserName(username)
        if (user.roles.any { it.name == RoleType.SYSADMIN }) {
            return true
        }
        //val requiredPermission = "${targetClass.simpleName}.${method.name}"
        //val userPermissions = permissionRepository.findByUserId(user.id)
        //return userPermissions.any { it.name == requiredPermission }
        return false
    }
}