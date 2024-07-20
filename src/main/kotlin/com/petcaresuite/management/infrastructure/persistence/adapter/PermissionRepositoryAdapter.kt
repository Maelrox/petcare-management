package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.port.output.PermissionPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Permission
import com.petcaresuite.management.infrastructure.persistence.mapper.PermissionEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaPermissionRepository
import org.springframework.stereotype.Component

@Component
class PermissionRepositoryAdapter(

    private val jpaPermissionRepository: JpaPermissionRepository,
    private val permissionMapper: PermissionEntityMapper
) : PermissionPersistencePort {

    override fun findByName(name: String): Permission? {
        TODO("Not yet implemented")
    }

    override fun save(permission: Permission): Permission? {
        val permissionEntity = permissionMapper.toEntity(permission)
        jpaPermissionRepository.save(permissionEntity)
        return permissionMapper.toDomain(permissionEntity)
    }

    override fun update(permission: Permission): Permission? {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): Permission {
        val permissionEntity = jpaPermissionRepository.findById(id)
            .orElseThrow { IllegalArgumentException(Responses.PERMISSION_NOT_FOUND) }
        return permissionMapper.toDomain(permissionEntity)
    }

}