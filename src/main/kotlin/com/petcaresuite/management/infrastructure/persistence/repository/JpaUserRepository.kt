package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.persistence.entity.UserEntity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JpaUserRepository : JpaRepository<UserEntity, Long> {
    fun save(user: User): UserEntity
    fun findByUsername(username: String): Optional<UserEntity>
}