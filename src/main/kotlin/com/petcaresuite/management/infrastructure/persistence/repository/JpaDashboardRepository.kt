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
        AND b.transaction_type = 'BILL'
        AND b.transaction_date >= DATE_TRUNC('month', CURRENT_DATE)
    """

        return jdbcTemplate.queryForObject(sql, { rs, _ ->
            InventorySalesDTO(
                amount = rs.getDouble("total_sales_amount"),
                currency = "COP"
            )
        }, companyId) ?: InventorySalesDTO(0.0, "COP")
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

    fun getInventoryTrends(companyId: Long): InventoryTrendDTO {
        val sql = """
        WITH current_month AS (
            SELECT COALESCE(SUM(b.total_amount), 0.0) as total_sales_amount
            FROM billing b
            JOIN billing_details bd ON b.billing_id = bd.billing_id
            JOIN inventory i ON bd.inventory_id = i.inventory_id
            WHERE b.company_id = ?
            AND b.transaction_type = 'BILL'
            AND DATE_TRUNC('month', b.transaction_date) = DATE_TRUNC('month', CURRENT_DATE)
        ),
        previous_month AS (
            SELECT COALESCE(SUM(b.total_amount), 0.0) as total_sales_amount
            FROM billing b
            JOIN billing_details bd ON b.billing_id = bd.billing_id
            JOIN inventory i ON bd.inventory_id = i.inventory_id
            WHERE b.company_id = ?
            AND b.transaction_type = 'BILL'
            AND DATE_TRUNC('month', b.transaction_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
        )
        SELECT 
            cm.total_sales_amount as current_sales,
            pm.total_sales_amount as previous_sales
        FROM current_month cm
        CROSS JOIN previous_month pm
    """

        return jdbcTemplate.queryForObject(sql, { rs, _ ->
            val currentSales = rs.getInt("current_sales")
            val previousSales = rs.getInt("previous_sales")
            val trend = calculateTrend(currentSales, previousSales)

            InventoryTrendDTO(
                totalInventory = currentSales,
                inventoryTrend = TrendDTO(
                    percentage = trend,
                    period = "last month"
                )
            )
        }, companyId, companyId) ?: InventoryTrendDTO(0, TrendDTO(0.0, "last month"))
    }

    private fun calculateTrend(current: Int, previous: Int): Double {
        if (previous == 0) return 0.0
        return ((current - previous).toDouble() / previous * 100)
            .toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    fun getHotMetrics(companyId: Long): HotmetricDTO {
        val peakHoursData = jdbcTemplate.query("""
        WITH hourly_counts AS (
            SELECT 
                EXTRACT(HOUR FROM consultation_date) as hour,
                COUNT(*) as consultation_count
            FROM consultations
            WHERE company_id = ?
            AND DATE_TRUNC('month', consultation_date) = DATE_TRUNC('month', CURRENT_DATE)
            AND status IN ('ATTENDED', 'PAID')
            GROUP BY EXTRACT(HOUR FROM consultation_date)
        ),
        max_count AS (
            SELECT MAX(consultation_count) as max_count
            FROM hourly_counts
        )
        SELECT 
            hour,
            consultation_count,
            ROUND((consultation_count * 100.0 / NULLIF(max_count, 0))::numeric, 2) as percentage
        FROM hourly_counts, max_count
        WHERE consultation_count = max_count
        ORDER BY hour ASC
        LIMIT 1
    """, { rs, _ ->
            Triple(
                rs.getInt("hour"),
                rs.getInt("consultation_count"),
                rs.getDouble("percentage")
            )
        }, companyId).firstOrNull() ?: Triple(0, 0, 0.0)

        // Calculate high traffic percentage for the peak hour
        val highTrafficPercentage = jdbcTemplate.queryForObject("""
        WITH peak_hour_consultations AS (
            SELECT COUNT(*) as peak_count
            FROM consultations
            WHERE company_id = ?
            AND DATE_TRUNC('month', consultation_date) = DATE_TRUNC('month', CURRENT_DATE)
            AND EXTRACT(HOUR FROM consultation_date) = ?
            AND status IN ('ATTENDED', 'PAID')
        ),
        total_consultations AS (
            SELECT COUNT(*) as total_count
            FROM consultations
            WHERE company_id = ?
            AND DATE_TRUNC('month', consultation_date) = DATE_TRUNC('month', CURRENT_DATE)
            AND status IN ('ATTENDED', 'PAID')
        )
        SELECT 
            CASE 
                WHEN COALESCE(total_count, 0) = 0 THEN 0
                ELSE ROUND((COALESCE(peak_count, 0) * 100.0 / total_count)::numeric, 2)
            END as traffic_percentage
        FROM peak_hour_consultations, total_consultations
    """, Double::class.java, companyId, peakHoursData.first, companyId)

        // Get total consultations from last day including paid ones
        val lastDayConsultations = jdbcTemplate.queryForObject("""
        SELECT COUNT(*) as total_consultations
        FROM consultations
        WHERE company_id = ?
        AND DATE_TRUNC('day', consultation_date) = DATE_TRUNC('day', CURRENT_DATE - INTERVAL '1 day')
        AND status IN ('ATTENDED', 'PAID')
    """, Int::class.java, companyId)

        // Format peak hour in 12-hour format with AM/PM
        val peakHourFormatted = if (peakHoursData.second == 0) {
            null  // No consultations found
        } else {
            when {
                peakHoursData.first == 0 -> "12 AM"
                peakHoursData.first < 12 -> "${peakHoursData.first} AM"
                peakHoursData.first == 12 -> "12 PM"
                else -> "${peakHoursData.first - 12} PM"
            }
        }

        return HotmetricDTO(
            peakHours = peakHourFormatted,
            highTraffic = highTrafficPercentage.toInt(),
            consultations = lastDayConsultations
        )
    }

    fun getEmployeeResume(companyId: Long): EmployeeResumeDTO {
        val roleCounts = jdbcTemplate.query("""
        SELECT r.name, COUNT(DISTINCT ur.user_id) as user_count
        FROM roles r
        LEFT JOIN user_roles ur ON r.id = ur.role_id
        LEFT JOIN users u ON ur.user_id = u.id
        WHERE r.company_id = ?
        GROUP BY r.name
    """, { rs, _ ->
            Pair(
                rs.getString("name"),
                rs.getInt("user_count")
            )
        }, companyId)

        val mapTotals = HashMap<String, Int>()
        roleCounts.forEach { (roleName, count) ->
            mapTotals[roleName] = count
        }

        return EmployeeResumeDTO(mapTotals)
    }

}