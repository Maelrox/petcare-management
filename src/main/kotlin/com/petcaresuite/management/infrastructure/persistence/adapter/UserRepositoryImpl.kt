package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.persistence.mapper.IUserMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaUserRepository
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class UserRepositoryImpl(
    private val userRepository: JpaUserRepository,
    private val userMapper: IUserMapper
) : UserPersistencePort {
    override fun getUserInfoByUsername(username: String): Optional<User> {
        val userEntity = userRepository.findByUsername(username.trim())
        return if (userEntity.isPresent) {
            val user = userMapper.toModel(userEntity.get())
            Optional.of(user)
        } else {
            Optional.empty()
        }
    }

    override fun save(user: User): User {
        val userEntity = userMapper.toEntity(user)
        val savedUserEntity = userRepository.save(userEntity)
        return userMapper.toModel(savedUserEntity)
    }

    override fun getById(id: Long): User {
        val userEntityOptional  = userRepository.findById(id)
        val userEntity = userEntityOptional.orElseThrow {
            throw NoSuchElementException("User with id $id not found")
        }
        return userMapper.toModel(userEntity)
    }

    fun findByUsername(username: String): User? {
        val userEntity = userRepository.findByUsername(username)
        return if (userEntity.isPresent) {
            userMapper.toModel(userEntity.get())
        } else {
            null
        }
    }

}