package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.infrastructure.persistence.entity.ModulesActionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaModuleActionRepository : JpaRepository<ModulesActionEntity, Long> {

    fun findByName(name: String): ModulesActionEntity?

    fun existsByNameAndModuleId(name: String, moduleId: Long): Boolean

}