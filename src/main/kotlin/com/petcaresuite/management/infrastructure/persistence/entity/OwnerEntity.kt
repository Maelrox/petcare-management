package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "Owners")
data class OwnerEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    val id: Long? = null,

    @Column(name = "identification", length = 64)
    val identification: String?,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "address")
    val address: String?,

    @Column(name = "phone", length = 15)
    val phone: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identification_type_id")
    val identificationType: IdentificationTypeEntity
)