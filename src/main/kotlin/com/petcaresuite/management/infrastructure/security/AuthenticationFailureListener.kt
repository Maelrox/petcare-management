package com.petcaresuite.management.infrastructure.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.stereotype.Component

@Component
class AuthenticationFailureListener(private val loggingAttemptAdapter: LoginAttemptAdapter) :
    ApplicationListener<AuthenticationFailureBadCredentialsEvent?> {

    @Autowired
    private val request: HttpServletRequest? = null

    override fun onApplicationEvent(e: AuthenticationFailureBadCredentialsEvent) {
        val xfHeader = request!!.getHeader("X-Forwarded-For")
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.remoteAddr)) {
            loggingAttemptAdapter.onAuthenticationFailure(request.remoteAddr)
        } else {
            loggingAttemptAdapter.onAuthenticationFailure(xfHeader.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0])
        }
    }

}