package com.petcaresuite.management.application.dto

import java.time.LocalDate

data class MonthlyConsultationCount(
    val monthDate: LocalDate,
    val totalConsultations: Int
)