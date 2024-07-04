package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.service.AuthenticationService
import com.petcaresuite.management.domain.model.AuthenticationRequest
import com.petcaresuite.management.domain.model.AuthenticationResponse
import com.petcaresuite.management.domain.model.RegisterDTO
import com.petcaresuite.management.domain.repository.UserRepository
import com.petcaresuite.management.infrastructure.persistence.entity.User
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class JwtAuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: AuthenticationService,
    private val authenticationManager: AuthenticationManager
) {

    fun register(dto: RegisterDTO): AuthenticationResponse {
        val user = User(
            username = dto.userName,
            password = passwordEncoder.encode(dto.password),
            roles = dto.roles
        )

        userRepository.save(user)
        val jwtToken = jwtService.generateToken(user.username)
        return AuthenticationResponse(token = jwtToken)
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password
            )
        )
        val user = userRepository.getUserInfoByUsername(request.username.toString()).orElseThrow()
        val jwtToken = jwtService.generateToken(user.username)
        return AuthenticationResponse(token = jwtToken)
    }
}