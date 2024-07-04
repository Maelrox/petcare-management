package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "Products")
data class ProductEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    val id: Long? = null,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "description")
    val description: String?,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(name = "quantity")
    val quantity: Int = 0
)