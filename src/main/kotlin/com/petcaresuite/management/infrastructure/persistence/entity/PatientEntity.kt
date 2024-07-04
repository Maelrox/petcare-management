package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "Patients")
data class PatientEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    val id: Long? = null,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "species", length = 50)
    val species: String?,

    @Column(name = "breed", length = 50)
    val breed: String?,

    @Column(name = "age")
    val age: Int?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    val owner: OwnerEntity
)