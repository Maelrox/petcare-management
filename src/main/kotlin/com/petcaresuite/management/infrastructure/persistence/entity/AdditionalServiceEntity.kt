package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "AdditionalServices")
data class AdditionalServiceEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    val id: Long? = null,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "description")
    val description: String?,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal
)