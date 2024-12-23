package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.input.EmployeeUseCase
import com.petcaresuite.management.application.security.Authorize
import com.petcaresuite.management.application.service.modules.ModuleActions
import com.petcaresuite.management.application.service.modules.Modules
import com.petcaresuite.management.infrastructure.security.PermissionRequired

import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/employee")
@Validated
class EmployeeController(private val employeeUseCase: EmployeeUseCase) {

    @PutMapping
    @Authorize
    fun update(@Valid @RequestBody dto: EmployeeUpdateDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(employeeUseCase.update(dto))
    }

    @PostMapping()
    @Authorize
    fun registerEmployee(@Valid @RequestBody dto: EmployeeRegisterDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(employeeUseCase.register(dto))
    }

    @PatchMapping("/{username}/enable")
    @Authorize
    fun enableUser(@PathVariable username: String): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(employeeUseCase.activateDeactiveUser(username, true))
    }

    @PatchMapping("/{username}/disable")
    @Authorize
    fun disableUser(@PathVariable username: String): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(employeeUseCase.activateDeactiveUser(username, false))
    }

    @GetMapping()
    @Authorize
    fun getAllRolesByFilter(@ModelAttribute filterDTO: EmployeeFilterDTO, @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "30") size: Int): ResponseEntity<PaginatedResponseDTO<UserDetailsDTO>> {
        val pageable = PageRequest.of(page, size)
        val result = employeeUseCase.getAllByFilter(filterDTO, pageable)

        val pageDTO = PageDTO(
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        )

        val paginatedResponse = PaginatedResponseDTO(
            data = result.content,
            pagination = pageDTO
        )

        return ResponseEntity.ok(paginatedResponse)
    }

}