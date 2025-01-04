package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.domain.model.Company

interface CompanyPersistencePort {

    fun save(company: Company): Company

    fun findCompanyByIdentification(id: String): Company?

    fun findCompanyByName(companyName: String): Company?

    fun findById(id: Long): Company?

    fun getMonthlyOwners(companyId: Long): List<MonthlyOwnerCount>

    fun getCurrentMonthOwners(companyId: Long): Int

    fun getPreviousMonthOwners(companyId: Long): Int

    fun getMonthlyConsultations(companyId: Long): List<MonthlyConsultationCount>

    fun getAllConsultationTrends(companyId: Long): ConsultationTrendDTO

    fun getInventorySales(companyId: Long): InventorySalesDTO

    fun getAppointmentsTrend(companyId: Long): AppointmentTrendDTO

    fun getMonthlyPatients(companyId: Long): List<MonthlyPatientCount>

    fun getPatientTrends(companyId: Long): PatientTrendDTO

    fun getInventoryTrend(companyId: Long): InventoryTrendDTO

    fun getHotmetrics(companyId: Long): HotmetricDTO

    fun getEmployeeResume(companyId: Long): EmployeeResumeDTO

    fun getServiceResume(companyId: Long): ServiceResumeDTO

    fun getAttentionResume(companyId: Long): List<AttentionResumeDTO>

    fun getProductResume(companyId: Long): List<ProductResumeDTO>

}