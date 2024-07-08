package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.input.UserUseCase
import com.petcaresuite.management.application.port.output.JwtPort
import com.petcaresuite.management.application.mapper.IUserDTOMapper
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.domain.service.UserValidationService
import com.petcaresuite.management.infrastructure.security.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userValidationService: UserValidationService,
    private val roleRepository: RolePersistencePort,
    private val userRepository: UserPersistencePort,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: IUserDTOMapper,
    private val jwtPort: JwtPort,
    private val userDetailsService: UserDetailsService,
) :
    UserUseCase {
    override fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO {
        validateUserRegistration(userRegisterDTO)
        val roles = retrieveRoles(userRegisterDTO.roles!!)
        userRegisterDTO.password = passwordEncoder.encode(userRegisterDTO.password!!)
        val user = userMapper.toUser(userRegisterDTO, roles)
        userRepository.save(user)
        val userDetailsDTO = userMapper.toUserDetailsDTO(user)
        val (jwtToken, expirationDate) = jwtPort.generateToken(user.username)
        return AuthenticationResponseDTO(
            token = jwtToken,
            expirationDate = expirationDate,
            userDetailsDTO = userDetailsDTO
        )
    }

    override fun update(userUpdateDTO: UserUpdateDTO): ResponseDTO {
        validateUserUpdate(userUpdateDTO)
        val user = userRepository.getById(userUpdateDTO.id!!)
        userUpdateDTO.password = passwordEncoder.encode(userUpdateDTO.password!!)
        setUpdatableFields(userUpdateDTO, user)
        userRepository.save(user)
        return ResponseDTO.generateSuccessResponse(true, "User Updated")
    }

    override fun getByToken(token: String): UserDetailsDTO {
        val username = jwtPort.extractUsername(token);
        val userDetails = userDetailsService.loadUserByUsername(username)
        jwtPort.validateToken(token, userDetails)
        val user = getUserByUserName(username)
        return userMapper.toUserDetailsDTO(user)
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
            roleRepository.findByName(RoleType.valueOf(roleName))
        }?.toSet() ?: emptySet()
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
        return userRepository.getUserInfoByUsername(username).get()
    }

}