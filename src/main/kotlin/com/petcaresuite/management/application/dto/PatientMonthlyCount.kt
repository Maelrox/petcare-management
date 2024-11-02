package com.petcaresuite.management.application.dto

import java.time.LocalDate

data class MonthlyPatientCount(
    val monthDate: LocalDate,
    val totalPatients: Int
)
