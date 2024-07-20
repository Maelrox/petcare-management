package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.PermissionModuleActionDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.port.input.PermissionModulesActionUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/permission/module-actions")
class PermissionModulesActionController(private val permissionModulesActionUseCase: PermissionModulesActionUseCase) {

    @PostMapping()
    @Authorize
    fun savePermissionModulesAction(@Valid @RequestBody dto: PermissionModuleActionDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionModulesActionUseCase.save(dto))
    }

}