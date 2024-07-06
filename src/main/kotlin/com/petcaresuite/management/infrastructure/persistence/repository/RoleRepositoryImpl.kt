package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.repository.IRoleRepository
import com.petcaresuite.management.domain.repository.IUserRepository
import com.petcaresuite.management.infrastructure.persistence.mapper.IRoleMapper
import com.petcaresuite.management.infrastructure.persistence.mapper.IUserMapper
import org.springframework.stereotype.Component
import java.util.*

@Component
class RoleRepositoryImpl(
    private val jpaRoleRepository: JpaRoleRepository,
    private val roleMapper: IRoleMapper
) : IRoleRepository {
    override fun findByName(name: RoleType): Role? {
        val role = jpaRoleRepository.findByName(name);
        return roleMapper.toModel(role!!)
    }


}