package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.CompanyDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.port.input.CompanyUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/company")
class CompanyController(private val companyUseCase: CompanyUseCase) {

    @PostMapping()
    @PreAuthorize("hasAuthority('SYSADMIN')")
    fun saveCompany(@Valid @RequestBody dto: CompanyDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(companyUseCase.save(dto))
    }

    @PutMapping("/{id}")
    @Authorize
    fun updateCompany(@Valid @RequestBody dto: CompanyDTO, @PathVariable id: Long): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(companyUseCase.update(dto, id))
    }

}