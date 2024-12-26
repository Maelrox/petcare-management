package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.exception.CompanyNotFoundException
import com.petcaresuite.management.application.mapper.CompanyMapper
import com.petcaresuite.management.application.port.input.CompanyUseCase
import com.petcaresuite.management.application.port.output.CompanyFileStoragePort
import com.petcaresuite.management.application.port.output.CompanyPersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.CompanyDomainService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CompanyService(
    private val companyDomainService: CompanyDomainService,
    private val companyPersistencePort: CompanyPersistencePort,
    private val companyMapper: CompanyMapper,
    private val userService: UserService,
    private val companyFileStoragePort: CompanyFileStoragePort
) :
    CompanyUseCase {

    @Value("\${file.storage-host}")
    private lateinit var storageHost: String

    @Transactional
    override fun update(companyDTO: CompanyDTO): ResponseDTO {
        val user = userService.getCurrentUser()
        val company = companyPersistencePort.findById(user.company!!.id)
            ?: throw CompanyNotFoundException(Responses.COMPANY_IDENTIFICATION_DOESNT_EXIST.format(user.company!!.id))
        validateUpdate(companyDTO, user, company, user.company!!.id)
        val updatableCompany = setUpdatableFields(companyDTO, company)
        companyPersistencePort.save(updatableCompany)
        return ResponseDTO(Responses.COMPANY_UPDATED)
    }

    override fun get(): CompanyDTO {
        val user = userService.getCurrentUser()
        return companyMapper.toDTO(user.company!!)
    }

    override fun getDashboard(): CompanyDashboardDTO? {
        val user = userService.getCurrentUser()
        val company = companyPersistencePort.findById(user.company!!.id)
        return getCompanyResume(company!!.id)
    }

    override fun uploadLogo(file: MultipartFile): CompanyDTO? {
        val companyId = userService.getCurrentUser().company!!.id

        val company = companyPersistencePort.findById(companyId)
        val filePath = companyFileStoragePort.store(file, companyId)
        val absolutePath = filePath.toAbsolutePath().toString()
        val fileName = absolutePath.substringAfterLast('/', absolutePath.substringAfterLast('\\'))
        company?.logoUrl = "$storageHost/${companyId}/$fileName"
        companyPersistencePort.save(company!!)
        return companyMapper.toDTO(company)
    }

    private fun validateUpdate(companyDTO: CompanyDTO, user: User, company: Company, companyId: Long) {
        companyDomainService.validateUserCompanyAccess(user, companyId)
        if (company.name != companyDTO.name) {
            companyDomainService.validateName(companyDTO.name)
        }
        if (company.companyIdentification != companyDTO.companyIdentification) {
            companyDomainService.validateCompanyIdentification(companyDTO.companyIdentification)
        }
    }

    private fun setUpdatableFields(companyDTO: CompanyDTO, company: Company): Company {
        return company.copy(
            companyIdentification = companyDTO.companyIdentification,
            name = companyDTO.name,
            users =  emptyList(),
            country = companyDTO.country,
            phone = companyDTO.phone,
            address = companyDTO.address,
            email = companyDTO.email,
        )
    }

    fun getCompanyResume(companyId: Long): CompanyDashboardDTO {

        val ownerTrends = companyDomainService.getOwnerTrends(companyId)
        val attentionsTrend = companyDomainService.getConsultationTrends(companyId)
        val inventorySales = companyDomainService.getInventorySales(companyId)
        val inventoryTrend = companyDomainService.getInventoryTrends(companyId)

        val appointmentsTrend = companyDomainService.getAppointmentsToday(companyId)
        val patientChartData = companyDomainService.getPatientChartData(companyId)

        val hotMetrics = companyDomainService.getHotmetrics(companyId)
        val company = companyPersistencePort.findById(companyId)
        val companyDTO = companyMapper.toDTO(company!!)
        val employeeResume = companyDomainService.getEmployeeResume(companyId)

        return CompanyDashboardDTO(
            totalCustomers = ownerTrends.totalOwners,
            customersTrend = ownerTrends.customersTrend,
            chartData = patientChartData,
            totalAttentions = attentionsTrend.totalAttentions,
            attentionsTrend = attentionsTrend.attentionsTrend,
            inventorySales = inventorySales,
            inventoryTrend = inventoryTrend.inventoryTrend,
            todayAppointments = appointmentsTrend.totalAppointments,
            todayAppointmentsTrend = appointmentsTrend.appointmentsTrend,
            hotMetric = hotMetrics,
            company = companyDTO,
            employeeResume = employeeResume
        )
    }

}