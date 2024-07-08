package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.RoleType


interface RolePersistencePort {
    fun findByName(name: RoleType): Role?
}