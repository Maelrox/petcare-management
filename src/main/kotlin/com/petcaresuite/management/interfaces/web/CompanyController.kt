package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.CompanyDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.port.input.CompanyUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/company")
class CompanyController(private val companyUseCase: CompanyUseCase) {

    @PutMapping()
    @Authorize
    fun updateCompany(@Valid @RequestBody companyDTO: CompanyDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(companyUseCase.update(companyDTO))
    }

    @GetMapping()
    @Authorize
    fun getCompany(): ResponseEntity<CompanyDTO> {
        return ResponseEntity.ok(companyUseCase.get())
    }

}