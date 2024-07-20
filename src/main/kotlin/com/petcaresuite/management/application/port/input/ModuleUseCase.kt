package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.ModuleDTO
import com.petcaresuite.management.application.dto.ResponseDTO

interface ModuleUseCase {

    fun save(moduleDTO: ModuleDTO): ResponseDTO

}