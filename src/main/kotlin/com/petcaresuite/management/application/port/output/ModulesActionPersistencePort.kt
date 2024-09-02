package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.ModulesAction

interface ModulesActionPersistencePort {

    fun findByName(name: String): ModulesAction?

    fun save(modulesAction: ModulesAction): ModulesAction?

    fun existsByNameAndModuleId(name: String, moduleId: Long): Boolean

    fun existsById(id: Long): Boolean

    fun getAllByIdIn(modulesActionIds: List<Long>): List<ModulesAction>

    fun getByPermissionIdAndModuleId(permissionId: Long, moduleId: Long): List<ModulesAction>


}