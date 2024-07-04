package com.petcaresuite.management.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "Appointments")
data class AppointmentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    val patient: PatientEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id")
    val vet: VetEntity,

    @Column(name = "appointment_date", nullable = false)
    val appointmentDate: LocalDateTime,

    @Column(name = "reason")
    val reason: String?,

    @Column(name = "status", length = 50)
    val status: String?
)