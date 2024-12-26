package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.CompanyDTO
import com.petcaresuite.management.application.dto.CompanyDashboardDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.port.input.CompanyUseCase
import com.petcaresuite.management.application.security.Authorize
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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

    @GetMapping("/dashboard")
    @Authorize
    fun getCompanyDashboard(): ResponseEntity<CompanyDashboardDTO> {
        return ResponseEntity.ok(companyUseCase.getDashboard())
    }

    @PatchMapping("/logo")
    @Authorize
    fun attachFile(
        @RequestParam("file") file: MultipartFile,
        request: HttpServletRequest
    ): ResponseEntity<CompanyDTO> {
        return ResponseEntity.ok(companyUseCase.uploadLogo(file))
    }

}