package com.petcaresuite.management.infrastructure.persistence.entity

import com.petcaresuite.management.domain.model.RoleType
import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class RoleEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    val name: RoleType
)