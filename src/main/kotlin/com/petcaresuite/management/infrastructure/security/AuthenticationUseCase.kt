package com.petcaresuite.management.infrastructure.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import com.petcaresuite.management.application.dto.AuthenticationRequestDTO
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.security.AuthenticationUseCase
import com.petcaresuite.management.application.security.LoginAttemptUseCase
import com.petcaresuite.management.application.mapper.UserMapper
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.application.security.CustomUserDetails
import com.petcaresuite.management.infrastructure.persistence.repository.JpaModuleActionRepository

@Service
class AuthenticationUseCase(
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val loggingAttemptService: LoginAttemptUseCase,
    private val userMapper: UserMapper,
    private val jpaModuleActionRepository: JpaModuleActionRepository,
) : AuthenticationUseCase {

    override fun authenticate(request: AuthenticationRequestDTO): AuthenticationResponseDTO {
        if (loggingAttemptService.isBlocked()) {
            throw IllegalAccessException(Responses.USER_LOGIN_TOO_MANY_ATTEMPTS)
        }
        val authenticationResponse = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.userName, request.password
            )
        )
        val customPrincipal = authenticationResponse.principal as CustomUserDetails
        val user = customPrincipal.user
        val (jwtToken, expirationDate) = jwtService.generateToken(user.username!!)

        val moduleActionIds = user.roles!!
            .flatMap { role -> role.permissions!! }
            .flatMap { permission -> permission.modulesAction!! }
            .map { moduleAction -> moduleAction.id }
            .toSet()
        val moduleActions = jpaModuleActionRepository.findAllById(moduleActionIds)
        val userDetailsDTO = userMapper.toLoginDTO(user, moduleActions)
        return AuthenticationResponseDTO(
            message = Responses.USER_AUTHENTICATED,
            token = jwtToken,
            expirationDate = expirationDate,
            userDetails = userDetailsDTO
        )
    }
}