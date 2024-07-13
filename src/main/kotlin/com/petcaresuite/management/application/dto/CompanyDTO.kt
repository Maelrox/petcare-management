package com.petcaresuite.management.application.dto

import jakarta.validation.constraints.*

data class CompanyDTO(
    @field:NotNull(message = "Company name is required")
    @field:Size(min = 3, max = 255, message = "Company length must be between 3 and 255 characters")
    val name: String,
    @field:NotNull(message = "Country is required")
    @field:Size(min = 3, max = 255, message = "Username length must be between 3 and 255 characters")
    val country: String?,
    @field:NotNull(message = "Company Identification")
    @field:Size(min = 3, max = 255, message = "Company identification length must be between 3 and 255 characters")
    val companyIdentification: String,
)
