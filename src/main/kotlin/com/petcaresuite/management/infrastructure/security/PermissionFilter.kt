package com.petcaresuite.management.infrastructure.security

import com.petcaresuite.management.application.service.ModuleService
import com.petcaresuite.management.application.service.PermissionService
import com.petcaresuite.management.application.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class PermissionCheckInterceptor : HandlerInterceptor {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var permissionService: PermissionService

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val method = handler.method

            val permissionRequired = method.getAnnotation(PermissionRequired::class.java)
            if (permissionRequired != null) {
                val module = permissionRequired.module
                val action = permissionRequired.action
                val hasPermission: Boolean = permissionService.hasPermission(module, action)
                if (!hasPermission) {
                    throw IllegalAccessException("You don't have permission for the operation")
                    return false
                }
            }
        }
        return true
    }
}