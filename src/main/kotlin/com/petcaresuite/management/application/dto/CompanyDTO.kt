package com.petcaresuite.management.application.dto

import com.petcaresuite.management.application.service.messages.Responses
import jakarta.validation.constraints.*

data class CompanyDTO(
    var id: Long? = 0,
    @field:NotNull(message = Responses.COMPANY_NAME_REQUIRED)
    @field:Size(min = 3, max = 255, message = Responses.COMPANY_NAME_LENGTH_INVALID)
    val name: String,
    @field:NotNull(message = Responses.COMPANY_COUNTRY_REQUIRED)
    @field:Size(min = 2, max = 255, message = Responses.COMPANY_COUNTRY_LENGTH_INVALID)
    val country: String?,
    @field:NotNull(message = Responses.COMPANY_IDENTIFICATION_REQUIRED)
    @field:Size(min = 3, max = 255, message = Responses.COMPANY_IDENTIFICATION_LENGTH_INVALID)
    val companyIdentification: String,
) {

}
