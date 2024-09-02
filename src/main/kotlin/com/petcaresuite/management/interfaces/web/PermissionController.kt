package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.input.PermissionUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
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

    @PutMapping()
    @Authorize
    fun updatePermission(@Valid @RequestBody dto: PermissionDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.update(dto))
    }

    @DeleteMapping("/{id}")
    @Authorize
    fun deletePermission(@PathVariable("id") id: Long): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.delete(id))
    }

    @PutMapping("/role")
    @Authorize
    fun savePermissionRole(@Valid @RequestBody dto: PermissionRolesDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.saveRoles(dto))
    }

    @PutMapping("/module")
    @Authorize
    fun savePermissionModule(@Valid @RequestBody dto: PermissionModulesDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.saveModules(dto))
    }

    @GetMapping()
    @Authorize
    fun getAllPermissionsByFilter(@ModelAttribute filterDTO: PermissionDTO, @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "30") size: Int): ResponseEntity<PaginatedResponseDTO<PermissionDTO>> {
        val pageable = PageRequest.of(page, size)
        val result = permissionUseCase.getAllByFilterPaginated(filterDTO, pageable)

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