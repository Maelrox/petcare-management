package com.petcaresuite.management.application.security

import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.infrastructure.security.AuthorizationService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class AuthorizationAspect(private val authorizationService: AuthorizationService) {

    @Around("@annotation(com.petcaresuite.management.application.security.Authorize) || @within(com.petcaresuite.management.application.security.Authorize)")
    fun authorize(joinPoint: ProceedingJoinPoint): Any {
        val signature = joinPoint.signature as MethodSignature
        val targetClass = joinPoint.target.javaClass
        val method = signature.method

        if (!authorizationService.isAuthorized(method, targetClass)) {
            throw IllegalAccessException(Responses.OPERATION_NOT_ALLOWED)
        }

        return joinPoint.proceed()
    }
}