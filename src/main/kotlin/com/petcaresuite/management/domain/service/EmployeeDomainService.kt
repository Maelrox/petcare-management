package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.dto.EmployeeRegisterDTO
import com.petcaresuite.management.application.dto.EmployeeUpdateDTO
import com.petcaresuite.management.application.dto.RoleDTO
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.application.security.CustomUserDetails
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

private const val PASSWORD_REGEX = """^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&\s]{8,}$"""

@Service
class EmployeeDomainService(
    private val userPersistencePort: UserPersistencePort,
    private val rolePersistencePort: RolePersistencePort,
) {

    fun validateUpdatePermission(employeeRegisterDTO: EmployeeUpdateDTO) {
        val currentUser: CustomUserDetails =
            SecurityContextHolder.getContext().authentication.principal as CustomUserDetails
        if (currentUser.getUserId() != employeeRegisterDTO.id && !SecurityContextHolder.getContext().authentication.authorities.any { it.authority == "ADMIN" }) {
            throw IllegalAccessException(Responses.USER_UPDATE_NOT_ALLOWED)
        }
    }

    fun validateActivationDeactivationPermission(currentUser: User, userToUpdate: User) {
        if (currentUser.companyId != userToUpdate.companyId) {
            throw IllegalAccessException(Responses.USER_UPDATE_INVALID_COMPANY)
        }
        if (!SecurityContextHolder.getContext().authentication.authorities.any { it.authority == "ADMIN" }) {
            throw IllegalAccessException(Responses.USER_UPDATE_NOT_ALLOWED)
        }
    }

    fun validateRoles(roles: Set<String>?) {
        roles?.forEach { roleName ->
            rolePersistencePort.findByName(roleName)
                ?: throw IllegalArgumentException(Responses.ROLE_NOT_FOUND.format(roleName))
        }
    }

    fun validateUpdateRoles(roles: Set<RoleDTO>?) {
        roles?.forEach { role ->
            rolePersistencePort.findByName(role.name!!)
                ?: throw IllegalArgumentException(Responses.ROLE_NOT_FOUND.format(role.name))
        }
    }

    fun validateUserDoesNotExist(username: String) {
        userPersistencePort.getUserInfoByUsername(username).takeIf { it.isPresent }
            ?.let { throw IllegalArgumentException(Responses.USER_ALREADY_EXISTS.format(username)) }
    }

    fun validatePasswordComplexity(password: String) {
        val passwordRegex = Regex(PASSWORD_REGEX)
        if (!password.matches(passwordRegex)) {
            throw IllegalArgumentException(Responses.USER_PASSWORD_NOT_VALID)
        }
    }


}