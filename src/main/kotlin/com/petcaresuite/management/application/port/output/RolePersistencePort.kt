package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RolePersistencePort {

    fun findByName(name: String): Role?

    fun save(role: Role): Role?

    fun saveAll(role: List<Role>): List<Role>?

    fun update(role: Role): Role?

    fun existsByNameAndCompanyId(name: String, id: Long): Boolean

    fun findById(id: Long): Role

    fun findAllByCompanyId(id: Long): List<Role>

    fun findAllByFilterPaginated(filterDTO: Role, pageable: Pageable, companyId: Long): Page<Role>

    fun delete(id: Long)
}