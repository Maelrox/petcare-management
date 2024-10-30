package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface UserPersistencePort {

    fun getUserInfoByUsername(username: String): Optional<User>

    fun save(user: User): User

    fun getById(id: Long): User

    fun findByUsername(username: String): User?

    fun findAllByFilterPaginated(filter: User, pageable: Pageable, companyId: Long): Page<User>

}