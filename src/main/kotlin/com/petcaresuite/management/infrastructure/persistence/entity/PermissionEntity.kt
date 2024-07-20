package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "permissions")
data class PermissionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = null,

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    val name: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "permission_modules_actions",
        joinColumns = [JoinColumn(name = "permission_id")],
        inverseJoinColumns = [JoinColumn(name = "module_action_id")]
    )
    val modulesAction: Set<ModulesActionEntity>? = emptySet()
)