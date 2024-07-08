package com.petcaresuite.management.infrastructure.security

import com.petcaresuite.management.application.security.LoginAttemptUseCase
import org.springframework.stereotype.Component

@Component
class LoginAttemptAdapter(private val loggingAttemptService: LoginAttemptUseCase) {

    fun onAuthenticationFailure(username: String?) {
        loggingAttemptService.loginFailed(username!!)
    }

    fun onAuthenticationSuccess(username: String?) {
        //TODO: Once transaction logging is implemented persist login audit-data
    }

    fun isBlocked(): Boolean {
        return loggingAttemptService.isBlocked()
    }
}