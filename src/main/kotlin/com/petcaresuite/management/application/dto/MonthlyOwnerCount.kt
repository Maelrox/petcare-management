package com.petcaresuite.management.application.dto

import java.time.LocalDate

data class MonthlyOwnerCount(
    val monthDate: LocalDate,
    val totalOwners: Int
)