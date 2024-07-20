package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

@Entity
@Table(name = "modules_actions")
data class ModulesActionEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    val module: ModuleEntity? = null,

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    val name: String,

    @NotNull
    @Column(name = "created_date", nullable = false)
    var createdDate: Instant? = null,

    @Column(name = "updated_date")
    val updatedDate: Instant? = null
)