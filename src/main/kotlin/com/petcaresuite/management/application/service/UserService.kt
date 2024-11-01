package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.CompanyMapper
import com.petcaresuite.management.application.mapper.UserMapper
import com.petcaresuite.management.application.port.input.UserUseCase
import com.petcaresuite.management.application.port.output.*
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.CompanyDomainService
import com.petcaresuite.management.domain.service.RoleDomainService
import com.petcaresuite.management.domain.service.UserDomainService
import com.petcaresuite.management.infrastructure.security.UserDetailsService
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userDomainService: UserDomainService,
    private val rolePersistencePort: RolePersistencePort,
    private val userPersistencePort: UserPersistencePort,
    private val moduleActionPersistencePort: ModulesActionPersistencePort,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper,
    private val jwtPort: JwtPort,
    private val userDetailsService: UserDetailsService,
    private val companyDomainService: CompanyDomainService,
    private val companyPersistencePort: CompanyPersistencePort,
    private val permissionPersistencePort: PermissionPersistencePort,
    private val roleDomainService: RoleDomainService,
    private val companyMapper: CompanyMapper,
) :
    UserUseCase {

    @Transactional
    override fun register(userRegisterDTO: UserRegisterDTO): AuthenticationResponseDTO {

        //Persist user
        validateUserRegistration(userRegisterDTO)
        userRegisterDTO.password = passwordEncoder.encode(userRegisterDTO.password!!)
        var user = userMapper.toDomain(userRegisterDTO, emptySet())
        user = userPersistencePort.save(user)

        //Persist company
        companyDomainService.validateCreation(userRegisterDTO.company, user)
        var company = companyMapper.toDomain(userRegisterDTO.company)
        company = companyPersistencePort.save(company)
        user.company = company

        //Persist Default Role and Permissions for the admin role
        val allModuleActions = moduleActionPersistencePort.getAll()
        var adminPermission = roleDomainService.getDefaultPermission(company, allModuleActions)
        adminPermission = permissionPersistencePort.save(adminPermission)!!
        var role = roleDomainService.getDefaultRole(company, allModuleActions, mutableSetOf(adminPermission))
        role = rolePersistencePort.save(role)!!
        user.roles = setOf(role)
        userPersistencePort.save(user)

        val (jwtToken, expirationDate) = jwtPort.generateToken(user.username!!)
        val userDetailsDTO = userMapper.toDTO(user)
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
            .orElseThrow { IllegalArgumentException(Responses.USER_NOT_VALID) }
    }

    override fun getByToken(token: String): UserDetailsDTO {
        val username = jwtPort.extractUsername(token)
        val userDetails = userDetailsService.loadUserByUsername(username)
        jwtPort.validateToken(token, userDetails)
        val user = getUserByUserName(username)
        return userMapper.toDTO(user)
    }

    private fun validateUserRegistration(userRegisterDTO: UserRegisterDTO) {
        if (userRegisterDTO.roles == null || userRegisterDTO.roles.size != 1 || userRegisterDTO.roles.first() != "ADMIN") {
            throw IllegalArgumentException(Responses.REGISTER_INVALID_ROLE)
        }
        userDomainService.validatePasswordComplexity(userRegisterDTO.password!!)
        userDomainService.validateUserDoesNotExist(userRegisterDTO.userName!!)
    }

    private fun validateUserUpdate(userUpdateDTO: UserUpdateDTO) {
        userDomainService.validateUpdatePermission(userUpdateDTO)
        userDomainService.validateRoles(userUpdateDTO.roles)
        userDomainService.validatePasswordComplexity(userUpdateDTO.password!!)
    }

    private fun retrieveRoles(roles: Set<String>): Set<Role> {
        return roles.mapNotNull { roleName ->
            rolePersistencePort.findByName(roleName)
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
                principal.username
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