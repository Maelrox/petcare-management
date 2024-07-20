package com.petcaresuite.management.application.port.input

interface AuthorizationUseCase {

    fun isAuthorized(operation: String, module: String): Boolean

}