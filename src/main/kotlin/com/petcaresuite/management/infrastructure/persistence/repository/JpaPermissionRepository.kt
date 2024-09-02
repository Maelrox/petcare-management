package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.domain.model.Permission
import com.petcaresuite.management.infrastructure.persistence.entity.PermissionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JpaPermissionRepository : JpaRepository<PermissionEntity, Long> {

    fun findByName(name: String): PermissionEntity?

    @Query(
        """
            SELECT p FROM PermissionEntity p 
            WHERE p.company.id = :companyId
            AND (:#{#filter.id} IS NULL OR p.id = :#{#filter.id})
            AND (
                :#{#filter.name} IS NULL 
                OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#filter.name}, '%'))
            ) 
            ORDER BY p.id desc 
            """
    )
    fun findAllByFilter(filter: Permission, pageable: Pageable, companyId: Long): Page<PermissionEntity>

    fun findAllByCompanyId(companyId: Long): Set<PermissionEntity>

    @Modifying
    @Query(value = "DELETE FROM role_permissions WHERE permission_id = :permissionId AND role_id IN :rolesIds", nativeQuery = true)
    fun deleteRemovedPermissions(permissionId: Long, rolesIds: List<Long>)

    @Modifying
    @Query(value = "DELETE FROM permission_modules_actions WHERE permission_id = :permissionId AND module_action_id NOT IN :moduleActionsId", nativeQuery = true)
    fun deleteRemovedModules(permissionId: Long, moduleActionsId: List<Long>)

    @Modifying
    @Query(value = "DELETE FROM permission_modules_actions WHERE permission_id = :permissionId", nativeQuery = true)
    fun deleteModuleAssociations(permissionId: Long): Int

    @Modifying
    @Query(value = "DELETE FROM role_permissions WHERE permission_id = :permissionId", nativeQuery = true)
    fun deleteRoleAssociations(permissionId: Long): Int

}