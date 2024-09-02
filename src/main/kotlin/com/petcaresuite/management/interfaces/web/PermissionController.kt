package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.input.PermissionUseCase
import com.petcaresuite.management.application.security.Authorize
import com.petcaresuite.management.application.service.modules.ModuleActions
import com.petcaresuite.management.application.service.modules.Modules
import com.petcaresuite.management.infrastructure.security.PermissionRequired
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/permission")
class PermissionController(private val permissionUseCase: PermissionUseCase) {

    @PostMapping()
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.CREATE)
    fun savePermission(@Valid @RequestBody dto: PermissionDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.save(dto))
    }

    @PutMapping()
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.UPDATE)
    fun updatePermission(@Valid @RequestBody dto: PermissionDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.update(dto))
    }

    @DeleteMapping("/{id}")
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.UPDATE)
    fun deletePermission(@PathVariable("id") id: Long): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.delete(id))
    }

    @PutMapping("/role")
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.ADD_ROLE)
    fun savePermissionRole(@Valid @RequestBody dto: PermissionRolesDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.saveRoles(dto))
    }

    @PutMapping("/module")
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.ADD_MODULE)
    fun savePermissionModule(@Valid @RequestBody dto: PermissionModulesDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.saveModules(dto))
    }

    @GetMapping()
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.VIEW)
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