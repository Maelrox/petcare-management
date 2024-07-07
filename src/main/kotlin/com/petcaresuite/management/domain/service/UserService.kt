package com.petcaresuite.management.domain.service

import com.petcaresuite.management.infrastructure.security.JwtTokenService
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.application.dto.UserRegisterDTO
import com.petcaresuite.management.application.port.input.IUserService
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.repository.IUserRepository
import com.petcaresuite.management.infrastructure.persistence.mapper.IRoleMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaRoleRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: IUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtTokenService,
    private val roleRepository: JpaRoleRepository,
    private val mapper: IRoleMapper
) : IUserService {
    override fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO {

        // Validations
        val roleEntities = userRegisterDTO.roles?.map { roleName ->
            roleRepository.findByName(RoleType.valueOf(roleName))
                ?: throw IllegalArgumentException("Role not found: $roleName")
        }!!?.toSet()
        userRepository.getUserInfoByUsername(userRegisterDTO.userName!!)
            .ifPresent { throw IllegalArgumentException("User ${userRegisterDTO.userName} already exists.") }

        // Additional data required
        val roles = roleEntities!!.map { role ->
            mapper.toModel(role)
        }.toSet()

        // Mapping DTO to domain object
        val user = User(
            username = userRegisterDTO.userName.toString(),
            password = passwordEncoder.encode(userRegisterDTO.password),
            email = userRegisterDTO.email.toString(),
            company = null,
            name = userRegisterDTO.name,
            createdDate = LocalDateTime.now(),
            enabled = true,
            roles = roles!!,
            country = userRegisterDTO.country,
            phone = userRegisterDTO.phone,
            lastModified = LocalDateTime.now()
        )

        userRepository.save(user)
        val jwtToken = jwtTokenService.generateToken(user.username)

        return AuthenticationResponseDTO(token = jwtToken)
    }

}