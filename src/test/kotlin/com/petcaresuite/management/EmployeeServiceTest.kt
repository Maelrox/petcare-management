package com.petcaresuite.management

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.EmployeeMapper
import com.petcaresuite.management.application.port.output.*
import com.petcaresuite.management.application.service.EmployeeService
import com.petcaresuite.management.application.service.UserService
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.EmployeeDomainService
import com.petcaresuite.management.domain.service.UserDomainService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class EmployeeServiceTest {

    @Mock
    private lateinit var userDomainService: UserDomainService

    @Mock
    private lateinit var rolePersistencePort: RolePersistencePort

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var employeeMapper: EmployeeMapper

    @Mock
    private lateinit var employeeDomainService: EmployeeDomainService

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var securityContext: SecurityContext

    @Mock
    private lateinit var authentication: Authentication

    private lateinit var employeeService: EmployeeService
    private lateinit var mockUser: User
    private lateinit var mockCompany: Company
    private lateinit var mockRole: Role
    private lateinit var mockEmployeeRegisterDTO: EmployeeRegisterDTO
    private lateinit var mockFilter: EmployeeFilterDTO

    @BeforeEach
    fun setUp() {
        mockCompany = Company(
            id = 1L,
            companyIdentification = "123456",
            name = "Test Company",
            country = "US",
            users = emptyList()
        )

        mockRole = Role(
            id = 1L,
            name = "EMPLOYEE",
            company = mockCompany,
            permissions = null
        )

        mockUser = User(
            id = 1L,
            email = "test@test.com",
            username = "test",
            password = "test",
            name = "john doe",
            phone = "123-456-7890",
            country = "CO",
            enabled = true,
            lastModified = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            company = mockCompany,
            roles = setOf(mockRole),
            companyId = null
        )

        mockEmployeeRegisterDTO = EmployeeRegisterDTO(
            id = null,
            username = "newemployee",
            password = "Password123!",
            email = "employee@test.com",
            name = "New Employee",
            phone = "987654321",
            country = "US",
            enabled = true,
            roles = "EMPLOYEE",
            companyId = 1L
        )

        mockFilter = EmployeeFilterDTO(
            name = "john",
            email = "test@test.com",
            companyId = null,
            userName = null,
            password = null,
            rol = null,
            country = null,
            enabled = null
        )

        employeeService = EmployeeService(
            userDomainService,
            rolePersistencePort,
            userPersistencePort,
            passwordEncoder,
            employeeMapper,
            employeeDomainService,
            userService
        )
        SecurityContextHolder.setContext(securityContext)
    }

    @Test
    fun `register - successful employee registration`() {
        // Given
        val encodedPassword = "Password123!"
        val roles = setOf(mockRole)

        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(passwordEncoder.encode(mockEmployeeRegisterDTO.password)).thenReturn(encodedPassword)
        Mockito.`when`(rolePersistencePort.findByName("EMPLOYEE")).thenReturn(mockRole)
        Mockito.`when`(employeeMapper.toDomain(mockEmployeeRegisterDTO, roles)).thenReturn(mockUser)

        // When
        val result = employeeService.register(mockEmployeeRegisterDTO)

        // Then
        Mockito.verify(employeeDomainService).validatePasswordComplexity(mockEmployeeRegisterDTO.password!!)
        Mockito.verify(employeeDomainService).validateUserDoesNotExist(mockEmployeeRegisterDTO.username!!)
        Mockito.verify(userPersistencePort).save(mockUser)
        assert(result.message == Responses.USER_CREATED)
        assert(result.success!!)
    }

    @Test
    fun `register - throws exception when no role provided`() {
        // Given
        val invalidDTO = mockEmployeeRegisterDTO.copy(roles = "")

        // When/Then
        val exception = assertThrows<IllegalArgumentException> {
            employeeService.register(invalidDTO)
        }
        assert(exception.message == Responses.REGISTER_NO_ROLE)
    }

    @Test
    fun `update - successful employee update`() {
        // Given
        val updateDTO = mockEmployeeRegisterDTO.copy(
            id = 1L,
            password = "NewPassword123!"
        )
        val encodedPassword = "NewPassword123!"

        Mockito.`when`(userPersistencePort.getById(1L)).thenReturn(mockUser)
        Mockito.`when`(passwordEncoder.encode(updateDTO.password)).thenReturn(encodedPassword)
        Mockito.`when`(rolePersistencePort.findByName("EMPLOYEE")).thenReturn(mockRole)

        // When
        val result = employeeService.update(updateDTO)

        // Then
        Mockito.verify(employeeDomainService).validateUpdatePermission(updateDTO)
        Mockito.verify(employeeDomainService).validatePasswordComplexity(updateDTO.password!!)
        Mockito.verify(userPersistencePort).save(mockUser)
        assert(result.message == Responses.USER_UPDATED)
        assert(result.success!!)
    }

    @Test
    fun `getAllByFilter - returns filtered and paginated results`() {
        // Given
        val filterDTO = EmployeeFilterDTO(
            name = "john",
            email = "test@test.com",
            userName = null,
            password = null,
            rol = null,
            country = "CO",
            enabled = null,
            companyId = null
        )
        val pageable = PageRequest.of(0, 10)

        val mockPage: Page<User> = PageImpl(listOf(mockUser))
        val mockUserDetailsDTO = UserDetailsDTO(
            id = 1L,
            username = "test",
            email = "test@test.com",
            name = "john doe",
            enabled = true,
            phone = "123-456-7890",
            country = "CO",
            companyId = null,
            roles = emptySet(),
            moduleActions = emptySet(),
            actions = emptySet()
        )

        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        Mockito.`when`(authentication.isAuthenticated).thenReturn(true)
        Mockito.`when`(authentication.principal).thenReturn(mockUser.username!!)
        Mockito.`when`(userPersistencePort.getUserInfoByUsername(mockUser.username!!)).thenReturn(Optional.of(mockUser))
        Mockito.`when`(employeeMapper.filterToDomain(filterDTO)).thenReturn(mockUser)
        Mockito.`when`(userPersistencePort.findAllByFilterPaginated(mockUser, pageable, 1L)).thenReturn(mockPage)
        Mockito.`when`(employeeMapper.toDTO(mockUser)).thenReturn(mockUserDetailsDTO)

        // When
        val result = employeeService.getAllByFilter(filterDTO, pageable)

        // Then
        assert(result.content.size == 1)
        assert(result.content[0].id == mockUserDetailsDTO.id)
        assert(result.content[0].username == mockUserDetailsDTO.username)
    }

    @Test
    fun `getByUserName - returns user when found`() {
        // Given
        val username = "test"
        Mockito.`when`(userPersistencePort.getUserInfoByUsername(username)).thenReturn(Optional.of(mockUser))

        // When
        val result = employeeService.getByUserName(username)

        // Then
        assert(result.id == mockUser.id)
        assert(result.username == mockUser.username)
    }

    @Test
    fun `getByUserName - throws exception when user not found`() {
        // Given
        val username = "nonexistent"
        Mockito.`when`(userPersistencePort.getUserInfoByUsername(username)).thenReturn(Optional.empty())

        // When/Then
        val exception = assertThrows<IllegalArgumentException> {
            employeeService.getByUserName(username)
        }
        assert(exception.message == Responses.USER_NOT_VALID)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}