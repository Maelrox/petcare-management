package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.dto.RoleDTO
import com.petcaresuite.management.application.port.input.RoleUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/role")
class RoleController(private val roleUseCase: RoleUseCase) {

    @PostMapping()
    @Authorize
    fun saveRole(@Valid @RequestBody dto: RoleDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(roleUseCase.save(dto))
    }

}