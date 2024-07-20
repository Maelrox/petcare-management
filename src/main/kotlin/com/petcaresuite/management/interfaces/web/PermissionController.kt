package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.PermissionDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.port.input.PermissionUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/permission")
class PermissionController(private val permissionUseCase: PermissionUseCase) {

    @PostMapping()
    @Authorize
    fun savePermission(@Valid @RequestBody dto: PermissionDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.save(dto))
    }

}