package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.infrastructure.persistence.entity.ModulesActionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaModuleActionRepository : JpaRepository<ModulesActionEntity, Long> {

    fun findByName(name: String): ModulesActionEntity?

    fun existsByNameAndModuleId(name: String, moduleId: Long): Boolean

    fun findAllByIdIn(modulesActionIds: List<Long>): List<ModulesActionEntity>

    @Query("""
        select ma.* from permission_modules_actions pma
        inner join modules_actions ma on ma.id = pma.module_action_id 
        where pma.permission_id = :permissionId and ma.module_id = :moduleId""", nativeQuery = true)
    fun findAllByPermissionIdAndModuleId(permissionId: Long, moduleId: Long): List<ModulesActionEntity>

}