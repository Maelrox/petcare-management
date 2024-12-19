package com.petcaresuite.management.infrastructure.persistence.entity

import com.petcaresuite.management.infrastructure.security.AESUtils
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.Base64

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
    var email: String,

    @Column(nullable = false)
    val name: String? = null,

    @Column(nullable = true)
    var phone: String? = null,

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: Set<RoleEntity> = setOf()
) {
    companion object {
        private fun isBase64(str: String): Boolean {
            return try {
                Base64.getDecoder().decode(str)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        private fun decryptIfEncrypted(value: String): String {
            return if (isBase64(value)) {
                try {
                    AESUtils.decrypt(value)
                } catch (e: Exception) {
                    value // Return original value if decryption fails
                }
            } else {
                value
            }
        }
    }

    @PrePersist
    @PreUpdate
    fun encryptFields() {
        // Only encrypt if the field is not already encrypted
        if (!isBase64(email)) {
            email = AESUtils.encrypt(email)
        }

        phone?.let {
            if (!isBase64(it)) {
                phone = AESUtils.encrypt(it)
            }
        }
    }

    @PostLoad
    fun decryptFields() {
        email = decryptIfEncrypted(email)
        phone = phone?.let { decryptIfEncrypted(it) }
    }

    override fun toString(): String {
        return "UserEntity(id=$id, username='$username', email='$email', name=$name, phone=$phone, country=$country, enabled=$enabled, lastModified=$lastModified, createdDate=$createdDate)"
    }
}