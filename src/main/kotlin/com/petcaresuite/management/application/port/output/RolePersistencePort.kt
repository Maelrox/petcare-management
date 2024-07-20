package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.Role

interface RolePersistencePort {

    fun findByName(name: String): Role?

    fun save(role: Role): Role?

    fun update(role: Role): Role?

    fun existsByNameAndCompanyId(name: String, id: Long): Boolean

    fun findById(id: Long): Role

    fun findAllByCompanyId(id: Long): Set<Role>

}