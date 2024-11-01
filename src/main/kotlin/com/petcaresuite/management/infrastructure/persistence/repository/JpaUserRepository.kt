package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.persistence.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JpaUserRepository : JpaRepository<UserEntity, Long> {
    fun save(user: User): UserEntity

    @Query(
        """
        SELECT u FROM UserEntity u 
        LEFT JOIN FETCH u.roles r 
        LEFT JOIN FETCH r.permissions p 
        WHERE u.username = :username
    """
    )
    fun findByUsername(@Param("username") username: String): Optional<UserEntity>

    @Query(
        """SELECT u FROM UserEntity u
            WHERE u.company.id = :#{#companyId} 
            AND (:#{#filter.name} IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :#{#filter.name}, '%'))) 
            ORDER by u.id desc 
        """
    )
    fun findAllByFilter(filter: User, pageable: Pageable, companyId: Long): Page<UserEntity>

}