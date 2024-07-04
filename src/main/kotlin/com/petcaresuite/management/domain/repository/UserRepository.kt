package com.petcaresuite.management.domain.repository

import com.petcaresuite.management.infrastructure.persistence.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Int> {
    fun getUserInfoByUsername(username: String): Optional<User>
}