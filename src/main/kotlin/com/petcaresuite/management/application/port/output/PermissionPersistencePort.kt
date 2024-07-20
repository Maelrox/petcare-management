package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.Permission

interface PermissionPersistencePort {

    fun findByName(name: String): Permission?

    fun save(permission: Permission): Permission?

    fun update(permission: Permission): Permission?

    fun findById(id: Long): Permission

}