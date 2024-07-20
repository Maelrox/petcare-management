package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.RoleMapper
import com.petcaresuite.management.application.port.input.RoleUseCase
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.CompanyValidationService
import com.petcaresuite.management.domain.service.RoleValidationService
import org.springframework.stereotype.Service

@Service
class RoleService(
    private val companyValidationService: CompanyValidationService,
    private val roleValidationService: RoleValidationService,
    private val userService: UserService,
    private val rolePersistencePort: RolePersistencePort,
    private val roleMapper: RoleMapper
) :
    RoleUseCase {
    override fun save(roleDTO: RoleDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        validateCreation(roleDTO, user)
        val role = roleMapper.toDomain(roleDTO)
        rolePersistencePort.save(role)
        return ResponseDTO(Responses.ROLE_CREATED)
    }

    override fun update(roleDTO: RoleDTO): ResponseDTO {
        TODO("Not yet implemented")
    }

    private fun validateCreation(roleDTO: RoleDTO, user: User) {
        companyValidationService.validateUserCompanyAccess(user, roleDTO.company?.id!!)
        roleValidationService.validateNameDuplicated(roleDTO.name!!, roleDTO.company.id)
    }

}