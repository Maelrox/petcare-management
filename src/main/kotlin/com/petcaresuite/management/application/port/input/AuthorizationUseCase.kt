package com.petcaresuite.management.application.port.input

import java.lang.reflect.Method

interface AuthorizationUseCase {
    fun isAuthorized(method: Method, targetClass: Class<*>): Boolean

}