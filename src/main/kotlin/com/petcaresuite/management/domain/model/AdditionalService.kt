package com.petcaresuite.management.domain.model

import java.math.BigDecimal

data class AdditionalService(
    val id: Long?,
    val name: String,
    val description: String?,
    val price: BigDecimal
)