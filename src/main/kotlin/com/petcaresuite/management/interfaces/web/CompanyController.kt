package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.CompanyDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.port.input.CompanyUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/company")
class CompanyController(private val companyUseCase: CompanyUseCase) {

    @PostMapping()
    @PreAuthorize("hasAuthority('SYSADMIN')")
    fun saveCompany(@Valid @RequestBody dto: CompanyDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(companyUseCase.save(dto))
    }

}