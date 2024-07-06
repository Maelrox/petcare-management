package com.petcaresuite.management.domain.model

data class Company(
    val id: Long,
    val name: String,
    val country: String?,
    val companyIdentification: String,
    val users: List<User>
) {
    constructor() : this(0, "", null, "", emptyList())
    constructor(id: Long) : this(
        id = id,
        name = "", // Provide default value for name
        country = null, // Provide default value for country
        companyIdentification = "", // Provide default value for companyIdentification
        users = emptyList() // Provide default value for users
    )

}