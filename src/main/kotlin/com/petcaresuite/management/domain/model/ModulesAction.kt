package com.petcaresuite.management.domain.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ModulesAction(
    val id: Long?,
    val name: String,
    val createdDate: Instant?,
    val updatedDate: Instant?,
    val moduleId: Long,
) {

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ModulesAction

        return id == other.id
    }
}