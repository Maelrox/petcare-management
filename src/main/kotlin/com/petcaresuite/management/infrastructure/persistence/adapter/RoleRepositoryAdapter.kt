package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.infrastructure.persistence.mapper.RoleEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaRoleRepository
import org.springframework.stereotype.Component

@Component
class RoleRepositoryAdapter(

    private val jpaRoleRepository: JpaRoleRepository,
    private val roleMapper: RoleEntityMapper
) : RolePersistencePort {
    override fun findByName(name: String): Role? {
        val role = jpaRoleRepository.findByName(name)
        return roleMapper.toDomain(role!!)
    }

    override fun save(role: Role): Role? {
        val roleEntity = roleMapper.toEntity(role)
        jpaRoleRepository.save(roleEntity)
        return roleMapper.toDomain(roleEntity)
    }

    override fun update(role: Role): Role? {
        val roleEntity = roleMapper.toEntity(role)
        jpaRoleRepository.save(roleEntity)
        return roleMapper.toDomain(roleEntity)
    }

    override fun existsByNameAndCompanyId(name: String, id: Long): Boolean {
        return jpaRoleRepository.existsByNameAndCompanyId(name, id)
    }

    override fun findById(id: Long): Role {
        val roleEntity = jpaRoleRepository.findById(id).orElseThrow {
            IllegalArgumentException(Responses.ROLE_NOT_FOUND)
        }
        return roleMapper.toDomain(roleEntity)
    }

    override fun findAllByCompanyId(id: Long): Set<Role> {
        val rolesEntity = jpaRoleRepository.findAllByCompanyId(id)
        return roleMapper.toDomainSet(rolesEntity)
    }

}