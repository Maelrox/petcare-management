package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.port.output.PermissionPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Permission
import com.petcaresuite.management.infrastructure.persistence.mapper.PermissionEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaPermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
        val permissionEntity = permissionMapper.toEntity(permission)
        jpaPermissionRepository.save(permissionEntity)
        return permissionMapper.toDomain(permissionEntity)
    }

    override fun findById(id: Long): Permission {
        val permissionEntity = jpaPermissionRepository.findById(id)
            .orElseThrow { IllegalArgumentException(Responses.PERMISSION_NOT_FOUND) }
        return permissionMapper.toDomain(permissionEntity)
    }

    override fun findAllByFilterPaginated(
        filter: Permission,
        pageable: Pageable,
        companyId: Long
    ): Page<Permission> {
        val pagedRolesEntity = jpaPermissionRepository.findAllByFilter(filter, pageable, companyId)
        return pagedRolesEntity.map { permissionMapper.toDomain(it) }
    }

    override fun findAllByCompanyId(companyId: Long): Set<Permission> {
       val permissionsEntity =  jpaPermissionRepository.findAllByCompanyId(companyId)
        return permissionMapper.toDomain(permissionsEntity)
    }

    override fun deleteRemovedRoles(roleId: Long, permissionIds: List<Long>) {
        jpaPermissionRepository.deleteRemovedPermissions(roleId, permissionIds)
    }

    override fun deleteRemovedModules(roleId: Long, permissionIds: List<Long>) {
        jpaPermissionRepository.deleteRemovedModules(roleId, permissionIds)
    }

    override fun delete(id: Long) {
        jpaPermissionRepository.deleteRoleAssociations(id)
        jpaPermissionRepository.deleteModuleAssociations(id);
        jpaPermissionRepository.deleteById(id)
    }

}