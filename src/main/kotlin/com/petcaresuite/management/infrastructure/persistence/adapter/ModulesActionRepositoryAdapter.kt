package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.port.output.ModulesActionPersistencePort

import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.ModulesAction
import com.petcaresuite.management.infrastructure.persistence.mapper.ModulesActionEntityMapper
import com.petcaresuite.management.infrastructure.persistence.repository.JpaModuleActionRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ModulesActionRepositoryAdapter(
    private val jpaModuleActionRepository: JpaModuleActionRepository,
    private val modulesActionMapper: ModulesActionEntityMapper
) : ModulesActionPersistencePort {

    override fun findByName(name: String): ModulesAction? {
        val moduleEntity = jpaModuleActionRepository.findByName(name)
            ?: throw IllegalArgumentException(Responses.MODULE_NOT_FOUND)
        return modulesActionMapper.toDomain(moduleEntity)
    }

    override fun save(modulesAction: ModulesAction): ModulesAction? {
        val moduleActionEntity = modulesActionMapper.toEntity(modulesAction)
        moduleActionEntity.createdDate = Instant.now()
        jpaModuleActionRepository.save(moduleActionEntity)
        return modulesActionMapper.toDomain(moduleActionEntity)
    }

    override fun existsByNameAndModuleId(name: String, moduleId: Long): Boolean {
        return jpaModuleActionRepository.existsByNameAndModuleId(name, moduleId)
    }

    override fun existsById(id: Long): Boolean {
        return jpaModuleActionRepository.existsById(id)
    }

}