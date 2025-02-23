package com.petcaresuite.management.domain.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.output.CompanyPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Product
import com.petcaresuite.management.domain.model.User
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

@Service
class CompanyDomainService(
    private val companyPersistencePort: CompanyPersistencePort,
) {

    fun validateCreation(companyDTO: CompanyDTO, user: User) {
        validateUserCompanyExistence(user)
        validateName(companyDTO.name)
        validateCompanyIdentification(companyDTO.companyIdentification)
    }

    fun validateName(companyName: String) {
        companyPersistencePort.findCompanyByName(companyName)?.let {
            throw IllegalArgumentException(Responses.COMPANY_NAME_ALREADY_EXIST.format(companyName))
        }
    }

    fun validateCompanyIdentification(id: String) {
        companyPersistencePort.findCompanyByIdentification(id)?.let {
            throw IllegalArgumentException(Responses.COMPANY_IDENTIFICATION_ALREADY_EXIST.format(id))
        }
    }

    fun validateUserCompanyExistence(user: User) {
        if (user.company != null) {
            throw IllegalArgumentException(Responses.USER_IS_MEMBER_OF_ANOTHER_COMPANY)
        }
    }

    fun validateUserCompanyAccess(user: User, id: Long) {
        if (id != user.company?.id) {
            throw IllegalAccessException(Responses.USER_IS_NOT_MEMBER_OF_COMPANY)
        }
    }

    fun getOwnerTrends(companyId: Long): OwnerTrendDTO {
        val currentMonth = companyPersistencePort.getCurrentMonthOwners(companyId)
        val previousMonth = companyPersistencePort.getPreviousMonthOwners(companyId)
        val trend = calculateTrend(currentMonth, previousMonth)
        return OwnerTrendDTO(
            totalOwners = currentMonth, customersTrend = TrendDTO(
                percentage = trend, period = "last month"
            )
        )
    }

    fun getOwnerChartData(companyId: Long): List<ChartDataDTO> {
        return companyPersistencePort.getMonthlyOwners(companyId).map { monthData ->
            ChartDataDTO(
                label = monthData.monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy")),
                value = monthData.totalOwners.toDouble()
            )
        }
    }

    private fun calculateTrend(current: Int, previous: Int): Double {
        if (previous == 0) return 0.0
        return ((current - previous).toDouble() / previous * 100).roundToTwoDecimals()
    }

    private fun Double.roundToTwoDecimals(): Double {
        return BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun getConsultationTrends(companyId: Long): ConsultationTrendDTO {
        return companyPersistencePort.getAllConsultationTrends(companyId)
    }

    fun getConsultChartData(companyId: Long): List<ChartDataDTO> {
        return companyPersistencePort.getMonthlyConsultations(companyId)
            .map { monthData ->
                ChartDataDTO(
                    label = monthData.monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy")),
                    value = monthData.totalConsultations.toDouble()
                )
            }
    }

    fun getPatientTrends(companyId: Long): PatientTrendDTO {
        return companyPersistencePort.getPatientTrends(companyId)
    }

    fun getPatientChartData(companyId: Long): List<ChartDataDTO> {
        return companyPersistencePort.getMonthlyPatients(companyId)
            .map { monthData ->
                ChartDataDTO(
                    label = monthData.monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy")),
                    value = monthData.totalPatients.toDouble()
                )
            }
    }

    fun getInventorySales(companyId: Long): InventorySalesDTO {
        return companyPersistencePort.getInventorySales(companyId)
    }

    fun getAppointmentsToday(companyId: Long): AppointmentTrendDTO {
        return companyPersistencePort.getAppointmentsTrend(companyId)
    }

    fun getInventoryTrends(companyId: Long): InventoryTrendDTO {
        return companyPersistencePort.getInventoryTrend(companyId)
    }

    fun getHotmetrics(companyId: Long): HotmetricDTO {
        return companyPersistencePort.getHotmetrics(companyId)
    }

    fun getEmployeeResume(companyId: Long): EmployeeResumeDTO {
        return companyPersistencePort.getEmployeeResume(companyId)
    }

    fun getServiceResume(companyId: Long): ServiceResumeDTO {
        return companyPersistencePort.getServiceResume(companyId)
    }

    fun getAttentionResume(companyId: Long): List<AttentionResumeDTO> {
        return companyPersistencePort.getAttentionResume(companyId)
    }

    fun getProductResume(companyId: Long): List<ProductResumeDTO> {
        return companyPersistencePort.getProductResume(companyId)
    }

}