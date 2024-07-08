package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.infrastructure.security.JwtService
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.application.port.input.IUserService
import com.petcaresuite.management.domain.mapper.IUserDTOMapper
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.repository.IUserRepository
import com.petcaresuite.management.domain.valueobject.CustomUserDetails
import com.petcaresuite.management.infrastructure.persistence.mapper.IRoleMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaRoleRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private const val PASSWORD_REGEX = """^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&\s]{8,}$"""

@Service
class UserService(
    private val userRepository: IUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtService,
    private val roleRepository: JpaRoleRepository,
    private val roleMapper: IRoleMapper,
    private val userMapper: IUserDTOMapper,
    private val customUserDetailsService: com.petcaresuite.management.infrastructure.security.UserDetailsService
    ) : IUserService {


    override fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO {
        validateUserRegistration(userRegisterDTO)
        val roles = retrieveRoles(userRegisterDTO.roles!!)
        userRegisterDTO.password = encodePassword(userRegisterDTO.password!!)
        val user = userMapper.toUser(userRegisterDTO, roles)
        userRepository.save(user)
        val jwtToken = jwtTokenService.generateToken(user.username)
        return AuthenticationResponseDTO(token = jwtToken)
    }

    override fun update(userUpdateDTO: UserUpdateDTO): ResponseDTO {
        validateUserUpdate(userUpdateDTO)
        val user = userRepository.getById(userUpdateDTO.id!!)
        userUpdateDTO.password = encodePassword(userUpdateDTO.password!!)
        setUpdatableFields(userUpdateDTO, user)
        userRepository.save(user)
        return ResponseDTO.generateSuccessResponse(true, "User Updated")
    }

    override fun getByToken(token: String): UserDetailsDTO {
        val username = jwtTokenService.extractUsername(token);
        val userDetails = customUserDetailsService.loadUserByUsername(username)
        jwtTokenService.validateToken(token, userDetails)
        val user = getUserByUserName(username)
        return userMapper.toUserDetailsDTO(user)
    }

    private fun getUserByUserName(username : String) : User {
        return userRepository.getUserInfoByUsername(username).get()
    }

    private fun setUpdatableFields(userUpdateDTO: UserUpdateDTO, user: User) {
        user.password = userUpdateDTO.password
        val roles = retrieveRoles(userUpdateDTO.roles!!)
        user.roles = roles
        user.country = userUpdateDTO.country
        user.enabled = userUpdateDTO.enabled!!
        user.lastModified = LocalDateTime.now()
    }

    private fun validateUserUpdate(userUpdateDTO: UserUpdateDTO) {
        validateUpdatePermission(userUpdateDTO)
        validateRoles(userUpdateDTO.roles)
        validatePasswordComplexity(userUpdateDTO.password!!)
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


    private fun retrieveRoles(roles: Set<String>): Set<Role> {
        return roles?.mapNotNull { roleName ->
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