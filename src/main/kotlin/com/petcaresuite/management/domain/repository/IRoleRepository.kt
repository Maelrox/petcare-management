package com.petcaresuite.management.domain.repository

import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.RoleType


interface IRoleRepository {
    fun findByName(name: RoleType): Role?
}