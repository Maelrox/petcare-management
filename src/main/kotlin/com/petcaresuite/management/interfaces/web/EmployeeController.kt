package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.input.EmployeeUseCase
import com.petcaresuite.management.application.security.Authorize
import com.petcaresuite.management.application.service.modules.ModuleActions
import com.petcaresuite.management.application.service.modules.Modules
import com.petcaresuite.management.infrastructure.security.PermissionRequired

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/employee")
@Validated
class EmployeeController(private val employeeUseCase: EmployeeUseCase) {

    @PutMapping
    @Authorize
    fun update(@Valid @RequestBody dto: EmployeeRegisterDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(employeeUseCase.update(dto))
    }

    @PostMapping()
    @PermissionRequired(Modules.ROLES, ModuleActions.DELETE)
    fun registerEmployee(@Valid @RequestBody dto: EmployeeRegisterDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(employeeUseCase.register(dto))
    }

}