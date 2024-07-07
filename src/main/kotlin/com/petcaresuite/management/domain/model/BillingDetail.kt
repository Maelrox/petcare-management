package com.petcaresuite.management.domain.model

import java.math.BigDecimal

data class BillingDetail(
    val detailId: Int?,
    val billingId: Int,
    val itemType: String,
    val itemId: Int?,
    val quantity: Int?,
    val amount: BigDecimal?,
    val medicine: Medicine? = null,
    val additionalService: AdditionalService? = null,
    val product: Product? = null
)