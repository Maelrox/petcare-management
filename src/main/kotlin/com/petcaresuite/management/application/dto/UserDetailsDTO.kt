package com.petcaresuite.management.application.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDetailsDTO(
    val id: Long?,
    val username: String?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val country: String?,
    val enabled: Boolean?,
    val companyId: Long?,
    val roles: Set<RoleDTO>?,
    val moduleActions: Set<ModulesActionDTO>?,
    val actions: Set<ModulesActionSimple>?

)
