package com.petcaresuite.management.domain.repository

import com.petcaresuite.management.domain.model.User
import java.util.Optional

interface IUserRepository {
    fun getUserInfoByUsername(username: String): Optional<User>
    fun save(user: User): User
    fun getById(id: Long): User
}