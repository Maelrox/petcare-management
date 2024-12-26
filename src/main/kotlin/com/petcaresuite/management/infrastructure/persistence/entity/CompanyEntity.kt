package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "companies")
data class CompanyEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val country: String? = null,

    @Column(name = "company_identification", nullable = false, unique = true)
    val companyIdentification: String,

    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val users: List<UserEntity>? = emptyList(),

    @Column(name="phone")
    val phone: String? = null,

    @Column(name="address")
    val address: String? = null,

    @Column(name="email")
    val email: String? = null,

    @Column(name="logo_url")
    var logoUrl: String? = null,

    )