package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.ModulesActionDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.mapper.ModulesActionMapper
import com.petcaresuite.management.application.port.input.ModulesActionUseCase
import com.petcaresuite.management.application.port.output.ModulesActionPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.service.ModulesActionValidationService
import org.springframework.stereotype.Service

@Service
class ModulesActionService(
    private val validationService: ModulesActionValidationService,
    private val modulesActionPersistencePort: ModulesActionPersistencePort,
    private val modulesActionMapper: ModulesActionMapper
) : ModulesActionUseCase {

    override fun save(modulesActionDTO: ModulesActionDTO): ResponseDTO {
        validationService.validateModuleId(modulesActionDTO.module!!.id!!)
        validationService.validateNameDuplicated(modulesActionDTO.name, modulesActionDTO.module!!.id!!)
        val module = modulesActionMapper.toDomain(modulesActionDTO)
        modulesActionPersistencePort.save(module)
        return ResponseDTO(Responses.MODULES_ACTION_CREATED.format(modulesActionDTO.name))
    }

}