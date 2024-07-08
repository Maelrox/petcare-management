package com.petcaresuite.management.application.security

interface ILoginAttemptService {
    fun loginFailed(key: String)
    fun isBlocked(): Boolean
}