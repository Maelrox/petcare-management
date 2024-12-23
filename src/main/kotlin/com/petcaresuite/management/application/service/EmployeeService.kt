package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.EmployeeMapper
import com.petcaresuite.management.application.port.input.EmployeeUseCase
import com.petcaresuite.management.application.port.output.*
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.EmployeeDomainService
import com.petcaresuite.management.domain.service.UserDomainService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmployeeService(
    private val userDomainService: UserDomainService,
    private val rolePersistencePort: RolePersistencePort,
    private val userPersistencePort: UserPersistencePort,
    private val passwordEncoder: PasswordEncoder,
    private val employeeMapper: EmployeeMapper,
    private val employeeDomainService: EmployeeDomainService,
    private val userService: UserService,
) :
    EmployeeUseCase {

    override fun register(employeeRegisterDTO: EmployeeRegisterDTO): ResponseDTO {
        validateEmployeeRegistration(employeeRegisterDTO)
        userDomainService.validateRoles(setOf(employeeRegisterDTO.roles))
        val roles = retrieveRoles(setOf(employeeRegisterDTO.roles))
        employeeRegisterDTO.password = passwordEncoder.encode(employeeRegisterDTO.password!!)
        val user = employeeMapper.toDomain(employeeRegisterDTO, roles)
        val userAdmin = userService.getCurrentUser()
        user.company = userAdmin.company
        userPersistencePort.save(user)
        return ResponseDTO(
            message = Responses.USER_CREATED,
            success = true
        )
    }

    override fun update(employeeUpdateDTO: EmployeeUpdateDTO): ResponseDTO {
        validateEmployeeUpdate(employeeUpdateDTO)
        val user = userPersistencePort.getById(employeeUpdateDTO.id!!)
        if (employeeUpdateDTO.password != null) {
            employeeUpdateDTO.password = passwordEncoder.encode(employeeUpdateDTO.password!!)
        }
        setUpdatableFields(employeeUpdateDTO, user)
        userPersistencePort.save(user)
        return ResponseDTO.generateSuccessResponse(true, Responses.USER_UPDATED)
    }

    override fun getByUserName(username: String): User {
        return userPersistencePort.getUserInfoByUsername(username)
            .orElseThrow { IllegalArgumentException(Responses.USER_NOT_VALID) }
    }

    override fun getAllByFilter(filterDTO: EmployeeFilterDTO, pageable: Pageable): Page<UserDetailsDTO> {
        val user = getCurrentUser()
        val filter = employeeMapper.filterToDomain(filterDTO)
        filter.companyId = user.company!!.id
        return userPersistencePort.findAllByFilterPaginated(filter, pageable, user.company!!.id).map { employeeMapper.toDTO(it) }
    }

    override fun activateDeactiveUser(username: String, isEnabled: Boolean): ResponseDTO? {
        val currentUser = getCurrentUser()
        val userToUpdate = userPersistencePort.findByUsername(username)
        employeeDomainService.validateActivationDeactivationPermission(currentUser, userToUpdate!!)
        userToUpdate.enabled = isEnabled
        userPersistencePort.save(userToUpdate)
        return ResponseDTO.generateSuccessResponse(true, Responses.USER_UPDATED)
    }

    private fun validateEmployeeRegistration(employeeRegisterDTO: EmployeeRegisterDTO) {
        if (employeeRegisterDTO.roles.isEmpty()) {
            throw IllegalArgumentException(Responses.REGISTER_NO_ROLE)
        }
        employeeDomainService.validatePasswordComplexity(employeeRegisterDTO.password!!)
        employeeDomainService.validateUserDoesNotExist(employeeRegisterDTO.username!!)
    }

    private fun validateEmployeeUpdate(employeeUpdateDTO: EmployeeUpdateDTO) {
        employeeDomainService.validateUpdatePermission(employeeUpdateDTO)
        employeeDomainService.validateUpdateRoles(employeeUpdateDTO.roles)
        if (employeeUpdateDTO.password != null) {
            employeeDomainService.validatePasswordComplexity(employeeUpdateDTO.password!!)
        }
    }

    private fun retrieveRoles(roles: Set<String>): Set<Role> {
        return roles.mapNotNull { roleName ->
            rolePersistencePort.findByName(roleName)
        }.toSet()
    }

    private fun retrieveUpdateRoles(roles: Set<RoleDTO>): Set<Role> {
        return roles.mapNotNull { role ->
            rolePersistencePort.findByName(role.name!!)
        }.toSet()
    }


    private fun setUpdatableFields(employeeUpdateDTO: EmployeeUpdateDTO, user: User) {
        if (employeeUpdateDTO.password != null) {
            user.password = employeeUpdateDTO.password
        }
        val roles = retrieveUpdateRoles(employeeUpdateDTO.roles)
        user.roles = roles
        user.country = employeeUpdateDTO.country
        user.enabled = employeeUpdateDTO.enabled!!
        user.lastModified = LocalDateTime.now()
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