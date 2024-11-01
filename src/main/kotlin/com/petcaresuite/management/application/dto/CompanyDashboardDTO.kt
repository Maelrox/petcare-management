package com.petcaresuite.management.application.dto

data class CompanyDashboardDTO(
    val totalCustomers: Int,
    val customersTrend: TrendDTO,
    val totalAttentions: Int,
    val attentionsTrend: TrendDTO,
    val inventorySales: InventorySalesDTO,
    val inventoryTrend: TrendDTO,
    val todayAppointments: Int,
    val todayAppointmentsTrend: TrendDTO,
    val chartData: List<ChartDataDTO>
)