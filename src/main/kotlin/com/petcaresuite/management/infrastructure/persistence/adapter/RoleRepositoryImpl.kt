package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.infrastructure.persistence.mapper.IRoleMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaRoleRepository
import org.springframework.stereotype.Component

@Component
class RoleRepositoryImpl(

    private val jpaRoleRepository: JpaRoleRepository,
    private val roleMapper: IRoleMapper
) : RolePersistencePort {
    override fun findByName(name: RoleType): Role? {
        val role = jpaRoleRepository.findByName(name);
        return roleMapper.toModel(role!!)
    }


}