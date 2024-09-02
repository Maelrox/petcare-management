package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.infrastructure.persistence.entity.ModuleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaModuleRepository : JpaRepository<ModuleEntity, Long> {

    fun findByName(name: String): ModuleEntity?

    fun existsByName(name: String): Boolean

    fun findAllByIdIn(modulesActionIds: List<Long>): List<ModuleEntity>

}