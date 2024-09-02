package com.petcaresuite.management.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RoleFilterDTO(
    val id: Long? = null,
    val name: String? = null,
)