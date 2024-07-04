package com.petcaresuite.management.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Billing(
    val id: Long?,
    val totalAmount: BigDecimal?,
    val paymentStatus: String?,
    val transactionType: String,
    val transactionDate: LocalDateTime
)