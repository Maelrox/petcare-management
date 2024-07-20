package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.infrastructure.persistence.entity.PermissionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaPermissionRepository : JpaRepository<PermissionEntity, Long> {

    fun findByName(name: String): PermissionEntity?

}