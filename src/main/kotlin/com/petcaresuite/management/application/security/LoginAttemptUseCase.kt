package com.petcaresuite.management.application.security

interface LoginAttemptUseCase {
    fun loginFailed(key: String)
    fun isBlocked(): Boolean
}