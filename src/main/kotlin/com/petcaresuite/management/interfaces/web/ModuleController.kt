package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.ModuleDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.port.input.ModuleUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/module")
class ModuleController(private val moduleUseCase: ModuleUseCase) {

    @PostMapping()
    @Authorize
    fun saveModule(@Valid @RequestBody dto: ModuleDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(moduleUseCase.save(dto))
    }
}