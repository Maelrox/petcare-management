package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.User
import java.util.Optional

interface UserPersistencePort {
    fun getUserInfoByUsername(username: String): Optional<User>
    fun save(user: User): User
    fun getById(id: Long): User

    fun findByUsername(username: String): User?
}