package com.petcaresuite.management.domain.service

import com.petcaresuite.management.infrastructure.security.JwtTokenService
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.application.dto.UserRegisterDTO
import com.petcaresuite.management.application.dto.UserUpdateDTO
import com.petcaresuite.management.application.port.input.IUserService
import com.petcaresuite.management.domain.mapper.IUserDTOMapper
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.repository.IUserRepository
import com.petcaresuite.management.domain.valueobject.CustomUserDetails
import com.petcaresuite.management.infrastructure.persistence.mapper.IRoleMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaRoleRepository
import com.petcaresuite.management.infrastructure.security.CustomUserDetailsService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private const val PASSWORD_REGEX = """^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&\s]{8,}$"""

@Service
class UserService(
    private val userRepository: IUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtTokenService,
    private val roleRepository: JpaRoleRepository,
    private val roleMapper: IRoleMapper,
    private val userMapper: IUserDTOMapper
) : IUserService {


    override fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO {
        validateUserRegistration(userRegisterDTO)
        val roles = retrieveRoles(userRegisterDTO)
        userRegisterDTO.password = encodePassword(userRegisterDTO.password!!)
        val user = userMapper.toUser(userRegisterDTO, roles)
        userRepository.save(user)
        val jwtToken = jwtTokenService.generateToken(user.username)
        return AuthenticationResponseDTO(token = jwtToken)
    }

    override fun update(userUpdateDTO: UserUpdateDTO): ResponseDTO {
        validateUserUpdate(userUpdateDTO)
        val user = userRepository.getById(userUpdateDTO.id!!)
        userRepository.save(user)
        return ResponseDTO.generateSuccessResponse(true, "User Updated")
    }

    private fun validateUserUpdate(userUpdateDTO: UserUpdateDTO) {
        validateUpdatePermission(userUpdateDTO)
        validateRoles(userUpdateDTO.roles)
        validatePasswordComplexity(userUpdateDTO.password!!)
        userUpdateDTO.password = encodePassword(userUpdateDTO.password!!)
    }

    private fun validateUpdatePermission(userUpdateDTO: UserUpdateDTO) {
        // TODO: ALLOW COMPANY ADMIN TO CHANGE ANY USER
        val currentUserId : CustomUserDetails = SecurityContextHolder.getContext().authentication.principal as CustomUserDetails
        // Move authorithies and roles rules in a different server it would be n
        if (currentUserId.getUserId() != userUpdateDTO.id && !SecurityContextHolder.getContext().authentication.authorities.any { it.authority == "SYSAD1MIN" }) {
            throw IllegalAccessException("Unauthorized: Only Application Admins or the user themselves can update the user.")
        }
    }

    private fun validateUserRegistration(userRegisterDTO: UserRegisterDTO) {
        validateRoles(userRegisterDTO.roles)
        validatePasswordComplexity(userRegisterDTO.password!!)
        validateUserDoesNotExist(userRegisterDTO.userName!!)
    }

    private fun validateRoles(roles: Set<String>?) {
        roles?.forEach { roleName ->
            roleRepository.findByName(RoleType.valueOf(roleName))
                ?: throw IllegalArgumentException("Role not found: $roleName")
        }
    }

    private fun validateUserDoesNotExist(username: String) {
        userRepository.getUserInfoByUsername(username)
            .takeIf { it.isPresent }
            ?.let { throw IllegalArgumentException("User $username already exists.") }
    }


    private fun retrieveRoles(userRegisterDTO: UserRegisterDTO): Set<Role> {
        return userRegisterDTO.roles?.mapNotNull { roleName ->
            roleRepository.findByName(RoleType.valueOf(roleName))
        }?.map { roleMapper.toModel(it) }?.toSet() ?: emptySet()
    }

    private fun validatePasswordComplexity(password: String) {
        val passwordRegex = Regex(PASSWORD_REGEX)
        if (!password.matches(passwordRegex)) {
            throw IllegalArgumentException("Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter and one special character.")
        }
    }

    private fun encodePassword(password: String): String {
        return passwordEncoder.encode(password)
    }

}