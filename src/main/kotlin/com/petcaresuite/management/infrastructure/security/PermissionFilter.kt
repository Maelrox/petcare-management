package com.petcaresuite.management.infrastructure.security

import com.petcaresuite.management.application.dto.UserDetailsDTO
import com.petcaresuite.management.application.mapper.UserMapper
import com.petcaresuite.management.application.service.PermissionService
import com.petcaresuite.management.domain.model.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class PermissionCheckInterceptor : HandlerInterceptor {

    @Autowired
    private lateinit var permissionService: PermissionService

    @Autowired
    private lateinit var userMapper: UserMapper

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val method = handler.method
            val permissionAnnotation = method.getAnnotation(PermissionRequired::class.java)
            var user: User? = null
            if (permissionAnnotation != null) {
                user = permissionService.getCurrentUser()
                val module = permissionAnnotation.module
                val action = permissionAnnotation.action
                val hasPermission = permissionService.hasPermission(user, module, action)
                if (!hasPermission) {
                    throw IllegalAccessException("You don't have permission for the operation")
                    return false
                }
            }
            val companyAnnotation = method.getAnnotation(SetCompany::class.java)
            if (companyAnnotation != null) {
                if (user == null) {
                    user = permissionService.getCurrentUser()
                }
            }
            if (user != null) {
                request.setAttribute("user", userMapper.toDTO(user))
            }
        }
        return true
    }
}