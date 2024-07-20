package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.ModulesActionDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.port.input.ModulesActionUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/module/action")
class ModulesActionController(private val modulesActionUseCase: ModulesActionUseCase) {

    @PostMapping()
    @Authorize
    fun saveModuleAction(@Valid @RequestBody dto: ModulesActionDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(modulesActionUseCase.save(dto))
    }
}