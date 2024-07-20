package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.ModuleDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.mapper.ModuleMapper
import com.petcaresuite.management.application.port.input.ModuleUseCase
import com.petcaresuite.management.application.port.output.ModulePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.service.ModuleValidationService
import org.springframework.stereotype.Service

@Service
class ModuleService(
    private val validationService: ModuleValidationService,
    private val modulePersistencePort: ModulePersistencePort,
    private val moduleMapper: ModuleMapper
) : ModuleUseCase {

    override fun save(moduleDTO: ModuleDTO): ResponseDTO {
        validationService.validateNameDuplicated(moduleDTO.name)
        val module = moduleMapper.toDomain(moduleDTO)
        modulePersistencePort.save(module)
        return ResponseDTO(Responses.MODULE_CREATED.format(moduleDTO.name))
    }

}