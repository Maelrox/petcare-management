package com.petcaresuite.management.domain.model

data class Role(
    val id: Long?,
    val name: String?,
    var company: Company?,
    var permissions: MutableSet<Permission>? = null
) {

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Role

        return id == other.id
    }
}
