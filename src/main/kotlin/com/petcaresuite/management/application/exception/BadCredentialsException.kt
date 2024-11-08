package com.petcaresuite.management.application.exception

import org.springframework.security.core.AuthenticationException

class BadCredentialsException(message: String) : AuthenticationException(message)
