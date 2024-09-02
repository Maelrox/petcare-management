package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val name: String? = null,

    @Column(nullable = true)
    val phone: String? = null,

    @Column(nullable = true)
    val country: String? = null,

    @Column(nullable = false)
    val enabled: Boolean = true,

    @Column(name = "last_modified", nullable = true)
    val lastModified: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_date", nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    val company: CompanyEntity? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: Set<RoleEntity> = setOf()
)
{
    override fun toString(): String {
        return "UserEntity(id=$id, username='$username', email='$email', name=$name, phone=$phone, country=$country, enabled=$enabled, lastModified=$lastModified, createdDate=$createdDate)"
    }
}