package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.UserMapper
import com.petcaresuite.management.application.port.input.UserUseCase
import com.petcaresuite.management.application.port.output.JwtPort
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.UserValidationService
import com.petcaresuite.management.infrastructure.security.UserDetailsService
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class UserService(
    private val userValidationService: UserValidationService,
    private val rolePersistencePort: RolePersistencePort,
    private val userPersistencePort: UserPersistencePort,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper,
    private val jwtPort: JwtPort,
    private val userDetailsService: UserDetailsService,
) :
    UserUseCase {
    override fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO {
        validateUserRegistration(userRegisterDTO)
        val roles = retrieveRoles(userRegisterDTO.roles!!)
        userRegisterDTO.password = passwordEncoder.encode(userRegisterDTO.password!!)
        val user = userMapper.toDomain(userRegisterDTO, roles)
        userPersistencePort.save(user)
        val userDetailsDTO = userMapper.toDTO(user)
        val (jwtToken, expirationDate) = jwtPort.generateToken(user.username)
        return AuthenticationResponseDTO(
            message = Responses.USER_CREATED,
            token = jwtToken,
            expirationDate = expirationDate,
            userDetailsDTO = userDetailsDTO
        )
    }

    override fun update(userUpdateDTO: UserUpdateDTO): ResponseDTO {
        validateUserUpdate(userUpdateDTO)
        val user = userPersistencePort.getById(userUpdateDTO.id!!)
        userUpdateDTO.password = passwordEncoder.encode(userUpdateDTO.password!!)
        setUpdatableFields(userUpdateDTO, user)
        userPersistencePort.save(user)
        return ResponseDTO.generateSuccessResponse(true, Responses.USER_UPDATED)
    }

    override fun getByUserName(username: String): User {
        return userPersistencePort.getUserInfoByUsername(username)
            .orElseThrow{ IllegalArgumentException(Responses.USER_NOT_VALID)}
    }

    override fun getByToken(token: String): UserDetailsDTO {
        val username = jwtPort.extractUsername(token)
        val userDetails = userDetailsService.loadUserByUsername(username)
        jwtPort.validateToken(token, userDetails)
        val user = getUserByUserName(username)
        return userMapper.toDTO(user)
    }

    private fun validateUserRegistration(userRegisterDTO: UserRegisterDTO) {
        userValidationService.validateRoles(userRegisterDTO.roles)
        userValidationService.validatePasswordComplexity(userRegisterDTO.password!!)
        userValidationService.validateUserDoesNotExist(userRegisterDTO.userName!!)
    }

    private fun validateUserUpdate(userUpdateDTO: UserUpdateDTO) {
        userValidationService.validateUpdatePermission(userUpdateDTO)
        userValidationService.validateRoles(userUpdateDTO.roles)
        userValidationService.validatePasswordComplexity(userUpdateDTO.password!!)
    }

    private fun retrieveRoles(roles: Set<String>): Set<Role> {
        return roles.mapNotNull { roleName ->
            rolePersistencePort.findByName(RoleType.valueOf(roleName))
        }.toSet()
    }

    private fun setUpdatableFields(userUpdateDTO: UserUpdateDTO, user: User) {
        user.password = userUpdateDTO.password
        val roles = retrieveRoles(userUpdateDTO.roles!!)
        user.roles = roles
        user.country = userUpdateDTO.country
        user.enabled = userUpdateDTO.enabled!!
        user.lastModified = LocalDateTime.now()
    }

    private fun getUserByUserName(username: String): User {
        return userPersistencePort.getUserInfoByUsername(username).get()
    }

    fun getCurrentUsername(): String? {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal: Any = authentication.principal
            return if (principal is UserDetails) {
                (principal).username
            } else {
                principal.toString()
            }
        }
        return null
    }

    fun getCurrentUser(): User {
        val userName = getCurrentUsername() ?: throw IllegalAccessException(Responses.USER_NOT_VALID)
        return getByUserName(userName)
    }

}