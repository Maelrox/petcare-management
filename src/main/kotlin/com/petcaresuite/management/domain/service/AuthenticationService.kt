package com.petcaresuite.management.domain.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import com.petcaresuite.management.application.dto.AuthenticationRequestDTO
import com.petcaresuite.management.application.service.JwtTokenService
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.service.IAuthenticationService
import com.petcaresuite.management.domain.repository.IUserRepository

@Service
class AuthenticationService(
    private val userRepository: IUserRepository,
    private val jwtService: JwtTokenService,
    private val authenticationManager: AuthenticationManager
) : IAuthenticationService {

    override fun authenticate(request: AuthenticationRequestDTO): AuthenticationResponseDTO {
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