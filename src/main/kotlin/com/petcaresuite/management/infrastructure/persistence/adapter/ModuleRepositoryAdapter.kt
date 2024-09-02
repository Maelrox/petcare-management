package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.port.output.ModulePersistencePort

import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.infrastructure.persistence.mapper.ModuleEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaModuleRepository
import com.petcaresuite.management.domain.model.Module
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ModuleRepositoryAdapter(
    private val jpaModuleRepository: JpaModuleRepository,
    private val moduleMapper: ModuleEntityMapper
) : ModulePersistencePort {

    override fun findByName(name: String): Module? {
        val moduleEntity = jpaModuleRepository.findByName(name)
            ?: throw IllegalArgumentException(Responses.MODULE_NOT_FOUND)
        return moduleMapper.toDomain(moduleEntity)
    }

    override fun save(module: Module): Module? {
        val moduleEntity = moduleMapper.toEntity(module)
        moduleEntity.createdDate = Instant.now()
        jpaModuleRepository.save(moduleEntity)
        return moduleMapper.toDomain(moduleEntity)
    }

    override fun existsByName(name: String): Boolean {
        return jpaModuleRepository.existsByName(name)
    }

    override fun existsById(id: Long): Boolean {
        return jpaModuleRepository.existsById(id)
    }

    override fun getAll(): List<Module> {
        return moduleMapper.toDomain(jpaModuleRepository.findAll())
    }

    override fun findAllById(modulesActionIds: List<Long>): List<Module> {
        return moduleMapper.toDomain(jpaModuleRepository.findAllByIdIn(modulesActionIds))
    }

}