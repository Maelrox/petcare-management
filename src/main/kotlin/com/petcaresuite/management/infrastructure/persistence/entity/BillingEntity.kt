package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "Billing")
data class BillingEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_id")
    val id: Long? = null,

    @Column(name = "total_amount", precision = 16, scale = 2)
    val totalAmount: BigDecimal?,

    @Column(name = "payment_status", length = 50)
    val paymentStatus: String?,

    @Column(name = "transaction_type", nullable = false, length = 50)
    val transactionType: String,

    @Column(name = "transaction_date")
    val transactionDate: LocalDateTime = LocalDateTime.now()
)