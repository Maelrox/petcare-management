package com.petcaresuite.management.domain.model

import java.time.LocalDateTime

data class Appointment(
    val id: Long?,
    val patient: Patient,
    val vet: Vet,
    val appointmentDate: LocalDateTime,
    val reason: String?,
    val status: String?
)