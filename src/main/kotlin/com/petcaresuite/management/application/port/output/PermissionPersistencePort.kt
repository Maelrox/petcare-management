package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PermissionPersistencePort {

    fun findByName(name: String): Permission?

    fun save(permission: Permission): Permission?

    fun update(permission: Permission): Permission?

    fun findById(id: Long): Permission

    fun findAllByFilterPaginated(filter: Permission, pageable: Pageable, companyId: Long): Page<Permission>

    fun findAllByCompanyId(id: Long): Set<Permission>

    fun deleteRemovedRoles(permissionId: Long, rolesIds: List<Long>)

    fun deleteRemovedModules(permissionId: Long, rolesIds: List<Long>)

    fun delete(id: Long)

}