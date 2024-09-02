package com.petcaresuite.management.domain.model

data class Permission(
    val id: Long? = null,
    val name: String?,
    var modulesAction: MutableSet<ModulesAction>? = null,
    var company: Company?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Permission

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}