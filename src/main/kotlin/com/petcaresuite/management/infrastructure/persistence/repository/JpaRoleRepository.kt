package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.domain.model.RoleType
import com.petcaresuite.management.infrastructure.persistence.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaRoleRepository : JpaRepository<RoleEntity, Long> {
    fun findByName(name: RoleType): RoleEntity?
}