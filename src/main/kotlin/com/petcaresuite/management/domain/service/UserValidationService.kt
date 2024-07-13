package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.valueobject.CustomUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

private const val PASSWORD_REGEX = """^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&\s]{8,}$"""

@Service
class UserValidationService(
    private val userPersistencePort: UserPersistencePort,
    private val rolePersistencePort: RolePersistencePort,
) {

    fun validateUpdatePermission(userUpdateDTO: UserUpdateDTO) {
        // TODO: ALLOW COMPANY ADMIN TO UPDATE ANY USER OF THE SAME COMPANY
        val currentUserId: CustomUserDetails =
            SecurityContextHolder.getContext().authentication.principal as CustomUserDetails
        // TODO: Context shouldn't depend of a single server
        if (currentUserId.getUserId() != userUpdateDTO.id && !SecurityContextHolder.getContext().authentication.authorities.any { it.authority == RoleType.SYSADMIN.toString() }) {
            throw IllegalAccessException(Responses.USER_UPDATE_NOT_ALLOWED)
        }
    }

    fun validateRoles(roles: Set<String>?) {
        roles?.forEach { roleName ->
            rolePersistencePort.findByName(RoleType.valueOf(roleName))
                ?: throw IllegalArgumentException(Responses.ROLE_NOT_FOUND.format(roleName))
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