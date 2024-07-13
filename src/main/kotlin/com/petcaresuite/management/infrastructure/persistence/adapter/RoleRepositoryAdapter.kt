package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.infrastructure.persistence.mapper.RoleEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaRoleRepository
import org.springframework.stereotype.Component

@Component
class RoleRepositoryAdapter(

    private val jpaRoleRepository: JpaRoleRepository,
    private val roleMapper: RoleEntityMapper
) : RolePersistencePort {
    override fun findByName(name: RoleType): Role? {
        val role = jpaRoleRepository.findByName(name);
        return roleMapper.toDomain(role!!)
    }


}