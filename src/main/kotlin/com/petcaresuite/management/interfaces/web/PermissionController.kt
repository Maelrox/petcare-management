package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.input.PermissionUseCase
import com.petcaresuite.management.application.security.Authorize
import com.petcaresuite.management.application.service.modules.ModuleActions
import com.petcaresuite.management.application.service.modules.Modules
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.infrastructure.security.PermissionRequired
import com.petcaresuite.management.infrastructure.security.SetCompany
import jakarta.servlet.http.HttpServletRequest
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
    fun savePermission(@Valid @RequestBody dto: PermissionDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val userDTO = request.getAttribute("user") as UserDetailsDTO
        val companyUserDTO = CompanyDTO(
            id = userDTO.companyId,
            name = "",
            country = "",
            companyIdentification = ""
        )
        dto.company = companyUserDTO
        return ResponseEntity.ok(permissionUseCase.save(dto))
    }

    @PutMapping()
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.UPDATE)
    fun updatePermission(@Valid @RequestBody dto: PermissionDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.update(dto))
    }

    @DeleteMapping("/{id}")
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.UPDATE)
    fun deletePermission(@PathVariable("id") id: Long, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.delete(id))
    }

    @PutMapping("/role")
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.ADD_ROLE)
    fun savePermissionRole(@Valid @RequestBody dto: PermissionRolesDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.saveRoles(dto))
    }

    @PutMapping("/module")
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.ADD_MODULE)
    fun savePermissionModule(@Valid @RequestBody dto: PermissionModulesDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(permissionUseCase.saveModules(dto))
    }

    @GetMapping()
    @Authorize
    @PermissionRequired(Modules.PERMISSIONS, ModuleActions.VIEW)
    fun getAllPermissionsByFilter(@ModelAttribute filterDTO: PermissionDTO, @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "30") size: Int, request: HttpServletRequest): ResponseEntity<PaginatedResponseDTO<PermissionDTO>> {
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

    @SetCompany
    @GetMapping("/hasPermission")
    fun hasPermission(@RequestParam module: String, @RequestParam action: String, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val user = request.getAttribute("user") as User
        val responseDTO = permissionUseCase.validatePermission(user, module, action)
        return ResponseEntity.ok(responseDTO)

    }
}