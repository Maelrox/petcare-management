package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.ModulesActionDTO
import com.petcaresuite.management.application.dto.ResponseDTO

interface ModulesActionUseCase {

    fun save(modulesActionDTO: ModulesActionDTO): ResponseDTO

}