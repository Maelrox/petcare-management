package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.persistence.mapper.UserEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class UserRepositoryAdapter(
    private val userRepository: JpaUserRepository,
    private val userMapper: UserEntityMapper
) : UserPersistencePort {
    override fun getUserInfoByUsername(username: String): Optional<User> {
        val userEntity = userRepository.findByUsername(username.trim())
        return if (userEntity.isPresent) {
            val user = userMapper.toDomain(userEntity.get())
            Optional.of(user)
        } else {
            Optional.empty()
        }
    }

    override fun save(user: User): User {
        val userEntity = userMapper.toEntity(user)
        val savedUserEntity = userRepository.save(userEntity)
        return userMapper.toDomain(savedUserEntity)
    }

    override fun getById(id: Long): User {
        val userEntityOptional  = userRepository.findById(id)
        val userEntity = userEntityOptional.orElseThrow {
            throw NoSuchElementException("User with id $id not found")
        }
        return userMapper.toDomain(userEntity)
    }

    override fun findByUsername(username: String): User? {
        val userEntity = userRepository.findByUsername(username)
        return if (userEntity.isPresent) {
            userMapper.toDomain(userEntity.get())
        } else {
            null
        }
    }

    override fun findAllByFilterPaginated(filter: User, pageable: Pageable, companyId: Long): Page<User> {
        val pagedRolesEntity = userRepository.findAllByFilter(filter, pageable, companyId)
        return pagedRolesEntity.map { userMapper.toDomain(it) }
    }

}