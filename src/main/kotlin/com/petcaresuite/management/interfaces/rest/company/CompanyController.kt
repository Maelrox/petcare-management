package com.petcaresuite.management.interfaces.rest.company

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/company")
class CompanyController {

    @PostMapping()
    @PreAuthorize("hasAuthority('admin')")
    fun userProfile(): String {
        return "User profile is shown here."
    }
}