package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "Vets")
data class VetEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vet_id")
    val id: Long? = null,

    @Column(name = "identification", nullable = false, length = 64)
    val identification: String,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "phone", length = 15)
    val phone: String?,

    @Column(name = "specialization", length = 100)
    val specialization: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identification_type_id")
    val identificationType: IdentificationTypeEntity
)