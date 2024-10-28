package com.petcaresuite.management

import com.petcaresuite.management.application.dto.CompanyDTO
import com.petcaresuite.management.application.mapper.CompanyMapper
import com.petcaresuite.management.application.port.output.CompanyPersistencePort
import com.petcaresuite.management.application.port.output.UserPersistencePort
import com.petcaresuite.management.application.service.CompanyService
import com.petcaresuite.management.application.service.UserService
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.CompanyDomainService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class CompanyServiceTest {

    @Mock
    private lateinit var companyDomainService: CompanyDomainService

    @Mock
    private lateinit var companyPersistencePort: CompanyPersistencePort

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    @Mock
    private lateinit var companyMapper: CompanyMapper

    @Mock
    private lateinit var userService: UserService

    private lateinit var companyService: CompanyService
    private lateinit var mockUser: User
    private lateinit var mockCompany: Company
    private lateinit var mockCompanyDTO: CompanyDTO

    @BeforeEach
    fun setUp() {
        mockCompany = Company(
            id = 1L,
            companyIdentification = "123456",
            name = "Test Company",
            country = "US",
            users = emptyList()
        )

        mockUser = User(
            id = 1L,
            email = "test@test.com",
            username = "test",
            password = "test",
            name = "john doe",
            phone = "123456",
            country = "colombia",
            enabled = true,
            lastModified = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            company = null,
            roles = emptySet()
        )

        mockCompanyDTO = CompanyDTO(
            companyIdentification = "123456",
            name = "Test Company",
            country = "US"
        )

        companyService = CompanyService(
            companyDomainService,
            companyPersistencePort,
            userPersistencePort,
            companyMapper,
            userService
        )
    }

    @Test
    fun `save - successful company creation`() {
        // Given
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(companyMapper.toDomain(mockCompanyDTO)).thenReturn(mockCompany)
        Mockito.`when`(companyPersistencePort.save(any(Company::class.java))).thenReturn(mockCompany)
        Mockito.doNothing().`when`(companyDomainService).validateUserCompanyExistence(mockUser)
        Mockito.doNothing().`when`(companyDomainService).validateName(mockCompanyDTO.name)
        Mockito.doNothing().`when`(companyDomainService).validateCompanyIdentification(mockCompanyDTO.companyIdentification)

        // When
        val result = companyService.save(mockCompanyDTO)

        // Then
        Mockito.verify(companyDomainService).validateUserCompanyExistence(mockUser)
        Mockito.verify(companyDomainService).validateName(mockCompanyDTO.name)
        Mockito.verify(companyDomainService).validateCompanyIdentification(mockCompanyDTO.companyIdentification)
        Mockito.verify(companyPersistencePort).save(any(Company::class.java))
        Mockito.verify(userPersistencePort).save(any(User::class.java))
        assert(result.message == Responses.COMPANY_CREATED)
    }

    @Test
    fun `save - throws exception when user already has company`() {
        // Given
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.doThrow(IllegalStateException("User already has a company"))
            .`when`(companyDomainService)
            .validateUserCompanyExistence(mockUser)

        // When/Then
        assertThrows<IllegalStateException> {
            companyService.save(mockCompanyDTO)
        }
    }

    @Test
    fun `update - successful company update`() {
        // Given
        val companyId = 1L
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(companyPersistencePort.findById(companyId)).thenReturn(mockCompany)
        Mockito.doNothing().`when`(companyDomainService).validateUserCompanyAccess(mockUser, companyId)

        // When
        val result = companyService.update(mockCompanyDTO, companyId)

        // Then
        Mockito.verify(companyDomainService).validateUserCompanyAccess(mockUser, companyId)
        Mockito.verify(companyPersistencePort).save(any(Company::class.java))
        assert(result.message == Responses.COMPANY_UPDATED)
    }

    @Test
    fun `update - throws exception when company not found`() {
        // Given
        val companyId = 1L
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(companyPersistencePort.findById(companyId)).thenReturn(null)

        // When/Then
        val exception = assertThrows<IllegalArgumentException> {
            companyService.update(mockCompanyDTO, companyId)
        }
        assert(exception.message == Responses.COMPANY_IDENTIFICATION_DOESNT_EXIST.format(companyId))
    }

    @Test
    fun `update - validates name when changed`() {
        // Given
        val companyId = 1L
        val updatedCompanyDTO = mockCompanyDTO.copy(name = "New Name")
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(companyPersistencePort.findById(companyId)).thenReturn(mockCompany)
        Mockito.doNothing().`when`(companyDomainService).validateUserCompanyAccess(mockUser, companyId)
        Mockito.doNothing().`when`(companyDomainService).validateName("New Name")

        // When
        companyService.update(updatedCompanyDTO, companyId)

        // Then
        Mockito.verify(companyDomainService).validateName("New Name")
    }

    @Test
    fun `update - validates company identification when changed`() {
        // Given
        val companyId = 1L
        val updatedCompanyDTO = mockCompanyDTO.copy(companyIdentification = "654321")
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(companyPersistencePort.findById(companyId)).thenReturn(mockCompany)
        Mockito.doNothing().`when`(companyDomainService).validateUserCompanyAccess(mockUser, companyId)
        Mockito.doNothing().`when`(companyDomainService).validateCompanyIdentification("654321")

        // When
        companyService.update(updatedCompanyDTO, companyId)

        // Then
        Mockito.verify(companyDomainService).validateCompanyIdentification("654321")
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}