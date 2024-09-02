package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.infrastructure.persistence.entity.RoleEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaRoleRepository : JpaRepository<RoleEntity, Long> {

    fun findByName(name: String): RoleEntity?

    fun existsByNameAndCompanyId(name: String, id: Long): Boolean

    fun findAllByCompanyId(id: Long): List<RoleEntity>

    @Query("SELECT r FROM RoleEntity r WHERE " +
            "r.company.id = :companyId and " +
            "(:#{#filter.id} IS NULL OR r.id = :#{#filter.id}) AND " +
            "(:#{#filter.name} IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :#{#filter.name}, '%'))) " +
            "order by r.id desc ")
    fun findAllByFilter(filter: Role, pageable: Pageable, companyId: Long): Page<RoleEntity>
}