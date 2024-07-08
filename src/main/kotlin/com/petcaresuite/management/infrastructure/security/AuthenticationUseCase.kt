package com.petcaresuite.management.infrastructure.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import com.petcaresuite.management.application.dto.AuthenticationRequestDTO
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.application.security.AuthenticationUseCase
import com.petcaresuite.management.application.security.LoginAttemptUseCase
import com.petcaresuite.management.application.mapper.IUserDTOMapper

@Service
class AuthenticationUseCase(
    private val userRepository: UserPersistencePort,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val loggingAttemptService: LoginAttemptUseCase,
    private val userMapper: IUserDTOMapper,
    ) : AuthenticationUseCase {

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
        val (jwtToken, expirationDate) = jwtService.generateToken(user.username)
        val userDetailsDTO = userMapper.toUserDetailsDTO(user)
        return AuthenticationResponseDTO(
            token = jwtToken,
            expirationDate = expirationDate,
            userDetailsDTO = userDetailsDTO
        )
    }
}