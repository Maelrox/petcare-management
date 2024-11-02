package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.application.dto.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.math.RoundingMode

@Repository
class JpaDashboardRepository(private val jdbcTemplate: JdbcTemplate) {

    fun getCurrentMonthOwners(companyId: Long): Int {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) as total_owners
            FROM owners 
            WHERE company_id = ?
            AND DATE_TRUNC('month', created_at::timestamp) = DATE_TRUNC('month', CURRENT_DATE)
        """, Int::class.java, companyId) ?: 0
    }

    fun getPreviousMonthOwners(companyId: Long): Int {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) as total_owners
            FROM owners 
            WHERE company_id = ?
            AND DATE_TRUNC('month', created_at::timestamp) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
        """, Int::class.java, companyId) ?: 0
    }

    fun getMonthlyOwners(companyId: Long): List<MonthlyOwnerCount> {
        return jdbcTemplate.query("""
            SELECT 
                DATE_TRUNC('month', created_at::timestamp)::DATE as month_date,
                COUNT(*) as total_owners
            FROM owners
            WHERE company_id = ?
            AND created_at::timestamp >= CURRENT_DATE - INTERVAL '6 months'
            GROUP BY DATE_TRUNC('month', created_at::timestamp)
            ORDER BY month_date DESC
        """, { rs, _ ->
            MonthlyOwnerCount(
                monthDate = rs.getDate("month_date").toLocalDate(),
                totalOwners = rs.getInt("total_owners")
            )
        }, companyId)
    }

    fun getCurrentMonthConsultations(companyId: Int): Int {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) as total_consultations
            FROM consultations 
            WHERE company_id = ?
            AND DATE_TRUNC('month', consultation_date) = DATE_TRUNC('month', CURRENT_DATE)
        """, Int::class.java, companyId) ?: 0
    }

    fun getPreviousMonthConsultations(companyId: Int): Int {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) as total_consultations
            FROM consultations 
            WHERE company_id = ?
            AND DATE_TRUNC('month', consultation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
        """, Int::class.java, companyId) ?: 0
    }

    fun getMonthlyConsultations(companyId: Long): List<MonthlyConsultationCount> {
        return jdbcTemplate.query("""
            SELECT 
                DATE_TRUNC('month', consultation_date)::DATE as month_date,
                COUNT(*) as total_consultations
            FROM consultations
            WHERE company_id = ?
            AND consultation_date >= CURRENT_DATE - INTERVAL '6 months'
            GROUP BY DATE_TRUNC('month', consultation_date)
            ORDER BY month_date DESC
        """, { rs, _ ->
            MonthlyConsultationCount(
                monthDate = rs.getDate("month_date").toLocalDate(),
                totalConsultations = rs.getInt("total_consultations")
            )
        }, companyId)
    }

    fun getAllConsultationTrends(companyId: Long): ConsultationTrendDTO {
        val sql = """
            WITH current_month AS (
                SELECT COUNT(*) as count
                FROM consultations 
                WHERE company_id = ?
                AND DATE_TRUNC('month', consultation_date) = DATE_TRUNC('month', CURRENT_DATE)
            ),
            previous_month AS (
                SELECT COUNT(*) as count
                FROM consultations 
                WHERE company_id = ?
                AND DATE_TRUNC('month', consultation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
            )
            SELECT 
                COALESCE(cm.count, 0) as current_count,
                COALESCE(pm.count, 0) as previous_count
            FROM current_month cm
            CROSS JOIN previous_month pm
        """

        return jdbcTemplate.queryForObject(sql, { rs, _ ->
            val current = rs.getInt("current_count")
            val previous = rs.getInt("previous_count")
            val trend = calculateTrend(current, previous)

            ConsultationTrendDTO(
                totalAttentions = current,
                attentionsTrend = TrendDTO(
                    percentage = trend,
                    period = "last month"
                )
            )
        }, companyId, companyId) ?: ConsultationTrendDTO(0, TrendDTO(0.0, "last month"))
    }

    fun getInventorySales(companyId: Long): InventorySalesDTO {
        val sql = """
        SELECT 
            COALESCE(SUM(b.total_amount), 0.0) as total_sales_amount
        FROM billing b
        JOIN billing_details bd ON b.billing_id = bd.billing_id
        JOIN inventory i ON bd.inventory_id = i.inventory_id
        WHERE b.company_id = ?
        AND b.transaction_type = 'SALE'
        AND b.transaction_date >= DATE_TRUNC('month', CURRENT_DATE)
    """

        return jdbcTemplate.queryForObject(sql, { rs, _ ->
            InventorySalesDTO(
                amount = rs.getDouble("total_sales_amount"),
                currency = "COP"
            )
        }, companyId) ?: InventorySalesDTO(0.0, "COP")
    }

    fun getTodayAppointments(companyId: Long): Int {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) as total_appointments
            FROM appointments 
            WHERE company_id = ?
            AND DATE_TRUNC('day', appointment_date) = DATE_TRUNC('day', CURRENT_DATE)
        """, Int::class.java, companyId) ?: 0
    }

    fun getPreviousDayAppointments(companyId: Long): Int {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) as total_appointments
            FROM appointments 
            WHERE company_id = ?
            AND DATE_TRUNC('day', appointment_date) = DATE_TRUNC('day', CURRENT_DATE - INTERVAL '1 day')
        """, Int::class.java, companyId) ?: 0
    }

    fun getAppointmentsTrend(companyId: Long): AppointmentTrendDTO {
        val sql = """
            WITH current_day AS (
                SELECT COUNT(*) as count
                FROM appointments 
                WHERE company_id = ?
                AND DATE_TRUNC('day', appointment_date) = DATE_TRUNC('day', CURRENT_DATE)
            ),
            previous_day AS (
                SELECT COUNT(*) as count
                FROM appointments 
                WHERE company_id = ?
                AND DATE_TRUNC('day', appointment_date) = DATE_TRUNC('day', CURRENT_DATE - INTERVAL '1 day')
            )
            SELECT 
                COALESCE(cd.count, 0) as current_count,
                COALESCE(pd.count, 0) as previous_count
            FROM current_day cd
            CROSS JOIN previous_day pd
        """

        return jdbcTemplate.queryForObject(sql, { rs, _ ->
            val current = rs.getInt("current_count")
            val previous = rs.getInt("previous_count")
            val trend = calculateTrend(current, previous)

            AppointmentTrendDTO(
                totalAppointments = current,
                appointmentsTrend = TrendDTO(
                    percentage = trend,
                    period = "yesterday"
                )
            )
        }, companyId, companyId) ?: AppointmentTrendDTO(0, TrendDTO(0.0, "yesterday"))
    }

    fun getMonthlyPatients(companyId: Long): List<MonthlyPatientCount> {
        return jdbcTemplate.query("""
            SELECT 
                DATE_TRUNC('month', p.created_at::timestamp)::DATE as month_date,
                COUNT(*) as total_patients
            FROM patients p
            JOIN owners o ON p.owner_id = o.owner_id
            WHERE o.company_id = ?
            AND p.created_at::timestamp >= CURRENT_DATE - INTERVAL '6 months'
            GROUP BY DATE_TRUNC('month', p.created_at::timestamp)
            ORDER BY month_date DESC
        """, { rs, _ ->
            MonthlyPatientCount(
                monthDate = rs.getDate("month_date").toLocalDate(),
                totalPatients = rs.getInt("total_patients")
            )
        }, companyId)
    }

    fun getCurrentMonthPatients(companyId: Long): Int {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) as total_patients
            FROM patients p
            JOIN owners o ON p.owner_id = o.owner_id
            WHERE o.company_id = ?
            AND DATE_TRUNC('month', p.created_at::timestamp) = DATE_TRUNC('month', CURRENT_DATE)
        """, Int::class.java, companyId) ?: 0
    }

    fun getPreviousMonthPatients(companyId: Long): Int {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) as total_patients
            FROM patients p
            JOIN owners o ON p.owner_id = o.owner_id
            WHERE o.company_id = ?
            AND DATE_TRUNC('month', p.created_at::timestamp) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
        """, Int::class.java, companyId) ?: 0
    }

    fun getPatientTrends(companyId: Long): PatientTrendDTO {
        val sql = """
            WITH current_month AS (
                SELECT COUNT(*) as count
                FROM patients p
                JOIN owners o ON p.owner_id = o.owner_id
                WHERE o.company_id = ?
                AND DATE_TRUNC('month', p.created_at::timestamp) = DATE_TRUNC('month', CURRENT_DATE)
            ),
            previous_month AS (
                SELECT COUNT(*) as count
                FROM patients p
                JOIN owners o ON p.owner_id = o.owner_id
                WHERE o.company_id = ?
                AND DATE_TRUNC('month', p.created_at::timestamp) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
            )
            SELECT 
                COALESCE(cm.count, 0) as current_count,
                COALESCE(pm.count, 0) as previous_count
            FROM current_month cm
            CROSS JOIN previous_month pm
        """

        return jdbcTemplate.queryForObject(sql, { rs, _ ->
            val current = rs.getInt("current_count")
            val previous = rs.getInt("previous_count")
            val trend = calculateTrend(current, previous)

            PatientTrendDTO(
                totalPatients = current,
                patientsTrend = TrendDTO(
                    percentage = trend,
                    period = "last month"
                )
            )
        }, companyId, companyId) ?: PatientTrendDTO(0, TrendDTO(0.0, "last month"))
    }

    private fun calculateTrend(current: Int, previous: Int): Double {
        if (previous == 0) return 0.0
        return ((current - previous).toDouble() / previous * 100)
            .toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }
}