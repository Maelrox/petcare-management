package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.input.RoleUseCase
import com.petcaresuite.management.application.security.Authorize
import com.petcaresuite.management.application.service.modules.ModuleActions
import com.petcaresuite.management.application.service.modules.Modules
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.security.PermissionRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/role")
class RoleController(private val roleUseCase: RoleUseCase) {

    @PostMapping()
    @PermissionRequired(Modules.ROLES, ModuleActions.CREATE)
    fun saveRole(@Valid @RequestBody dto: RoleDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val user = request.getAttribute("user") as UserDetailsDTO
        return ResponseEntity.ok(roleUseCase.save(dto))
    }

    @PutMapping()
    @PermissionRequired(Modules.ROLES, ModuleActions.UPDATE)
    fun updateRole(@Valid @RequestBody dto: RoleDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(roleUseCase.update(dto))
    }

    @DeleteMapping("/{id}")
    @PermissionRequired(Modules.ROLES, ModuleActions.DELETE)
    fun deleteRole(@PathVariable id: Long): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(roleUseCase.delete(id))
    }

    @GetMapping()
    @PermissionRequired(Modules.ROLES, ModuleActions.VIEW)
    fun getAllRolesByFilter(@ModelAttribute filterDTO: RoleFilterDTO, @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "30") size: Int): ResponseEntity<PaginatedResponseDTO<RoleDTO>> {
        val pageable = PageRequest.of(page, size)
        val result = roleUseCase.getAllByFilterPaginated(filterDTO, pageable)

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

    @GetMapping("/all")
    @Authorize
    @PermissionRequired(Modules.ROLES, ModuleActions.VIEW)
    fun getAllRoles(): ResponseEntity<List<RoleDTO>> {
        val result = roleUseCase.getAllByFilter()
        return ResponseEntity.ok(result)
    }

}