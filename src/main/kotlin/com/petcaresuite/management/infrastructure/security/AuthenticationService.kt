package com.petcaresuite.management.infrastructure.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import com.petcaresuite.management.application.dto.AuthenticationRequestDTO
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.security.IAuthenticationService
import com.petcaresuite.management.application.security.ILoginAttemptService
import com.petcaresuite.management.domain.repository.IUserRepository

@Service
class AuthenticationService(
    private val userRepository: IUserRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val loggingAttemptService: ILoginAttemptService
) : IAuthenticationService {

    override fun authenticate(request: AuthenticationRequestDTO): AuthenticationResponseDTO {
        if (loggingAttemptService.isBlocked()) {
            throw IllegalAccessException("Too many attempts wait 24 hours");
        }
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.userName, request.password
            )
        )
        val user = userRepository.getUserInfoByUsername(request.userName.toString()).orElseThrow()
        val jwtToken = jwtService.generateToken(user.username)
        return AuthenticationResponseDTO(token = jwtToken)
    }
}