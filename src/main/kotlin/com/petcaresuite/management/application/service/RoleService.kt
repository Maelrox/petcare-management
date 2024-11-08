package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.RoleMapper
import com.petcaresuite.management.application.port.input.RoleUseCase
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.CompanyDomainService
import com.petcaresuite.management.domain.service.RoleDomainService
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RoleService(
    private val companyDomainService: CompanyDomainService,
    private val roleValidationService: RoleDomainService,
    private val userService: UserService,
    private val rolePersistencePort: RolePersistencePort,
    private val roleMapper: RoleMapper,
    private val roleDomainService: RoleDomainService
) :
    RoleUseCase {
    override fun save(roleDTO: RoleDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        roleDTO.company = CompanyDTO(user.company!!.id, user.company!!.name, user.company!!.country, user.company!!.companyIdentification)
        validate(roleDTO, user)
        val role = roleMapper.toDomain(roleDTO)
        rolePersistencePort.save(role)
        return ResponseDTO(Responses.ROLE_CREATED)
    }

    override fun update(roleDTO: RoleDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        roleDTO.company = CompanyDTO(user.company!!.id, user.company!!.name, user.company!!.country, user.company!!.companyIdentification)
        validate(roleDTO, user)
        val role = roleMapper.toDomain(roleDTO)
        rolePersistencePort.update(role)
        return ResponseDTO(Responses.ROLE_UPDATED)
    }

    override fun getAllByFilter(): List<RoleDTO>? {
        val user = userService.getCurrentUser()
        var roles = rolePersistencePort.findAllByCompanyId(user.company!!.id);
        return roleMapper.toDTO(roles)
    }

    override fun getAllByFilterPaginated(filterDTO: RoleFilterDTO, pageable: Pageable): Page<RoleDTO> {
        val user = userService.getCurrentUser()
        var filter = roleMapper.toDomain(filterDTO)
        return rolePersistencePort.findAllByFilterPaginated(filter, pageable, user.company!!.id).map { roleMapper.toDTO(it) }
    }

    @Transactional
    override fun delete(id: Long): ResponseDTO {
        val user = userService.getCurrentUser()
        val role = rolePersistencePort.findById(id)
        roleDomainService.validateDeletion(role, user)
        rolePersistencePort.delete(id)
        return ResponseDTO(Responses.ROLE_DELETED)
    }

    private fun validate(roleDTO: RoleDTO, user: User) {
        companyDomainService.validateUserCompanyAccess(user, roleDTO.company?.id!!)
        roleValidationService.validateNameDuplicated(roleDTO.name!!, roleDTO.company!!.id!!)
    }

}