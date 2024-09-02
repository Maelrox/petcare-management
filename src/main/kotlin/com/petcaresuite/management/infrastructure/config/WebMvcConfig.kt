package com.petcaresuite.management.infrastructure.config

import com.petcaresuite.management.infrastructure.security.PermissionCheckInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebMvcConfig : WebMvcConfigurer {

    @Autowired
    private val permissionCheckInterceptor: PermissionCheckInterceptor? = null

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(permissionCheckInterceptor!!)
    }
}