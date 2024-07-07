package com.petcaresuite.management.infrastructure.persistence.entity

import java.math.BigDecimal
import jakarta.persistence.*

@Entity
@Table(name = "billing_details")
data class BillingDetailEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    val detailId: Int? = null,

    @Column(name = "billing_id")
    val billingId: Int,

    @Column(name = "item_type", nullable = false)
    val itemType: String,

    @Column(name = "item_id")
    val itemId: Int? = null,

    @Column(name = "quantity")
    val quantity: Int? = null,

    @Column(name = "amount")
    val amount: BigDecimal? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "medicine_id", insertable = false, updatable = false)
    val medicine: MedicineEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "service_id", insertable = false, updatable = false)
    val additionalService: AdditionalServiceEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "product_id", insertable = false, updatable = false)
    val product: ProductEntity? = null
)