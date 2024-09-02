package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

@Entity
@Table(name = "modules")
data class ModuleEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = null,

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    val name: String,

    @NotNull
    @Column(name = "created_date", nullable = false)
    var createdDate: Instant? = null,

    @Column(name = "updated_date")
    val updatedDate: Instant? = null,

    @OneToMany(mappedBy = "module", fetch = FetchType.EAGER)
    val modulesActionEntities: List<ModulesActionEntity>?
)