package com.petcaresuite.management

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.*
import com.petcaresuite.management.application.port.output.*
import com.petcaresuite.management.application.service.ModuleService
import com.petcaresuite.management.application.service.PermissionService
import com.petcaresuite.management.application.service.UserService
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.*
import com.petcaresuite.management.domain.service.PermissionDomainService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

@ExtendWith(MockitoExtension::class)
class PermissionServiceTest {

    @Mock
    private lateinit var permissionDomainService: PermissionDomainService

    @Mock
    private lateinit var permissionPersistencePort: PermissionPersistencePort

    @Mock
    private lateinit var rolePersistencePort: RolePersistencePort

    @Mock
    private lateinit var modulesActionPersistencePort: ModulesActionPersistencePort

    @Mock
    private lateinit var modulePersistencePort: ModulePersistencePort

    @Mock
    private lateinit var permissionMapper: PermissionMapper

    @Mock
    private lateinit var modulesActionMapper: ModulesActionMapper

    @Mock
    private lateinit var companyMapper: CompanyMapper

    @Mock
    private lateinit var userMapper: UserMapper

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var moduleService: ModuleService

    @Mock
    private lateinit var securityContext: SecurityContext

    @Mock
    private lateinit var authentication: Authentication

    private lateinit var permissionService: PermissionService

    private lateinit var mockCompany: Company
    private lateinit var mockUser: User
    private lateinit var mockPermission: Permission
    private lateinit var mockRole: Role
    private lateinit var mockModulesAction: ModulesAction
    private lateinit var mockModule: Module

    @BeforeEach
    fun setUp() {
        permissionService = PermissionService(
            permissionDomainService,
            permissionPersistencePort,
            rolePersistencePort,
            modulesActionPersistencePort,
            modulePersistencePort,
            permissionMapper,
            modulesActionMapper,
            companyMapper,
            userMapper,
            userService
        )

        // Initialize mock data
        mockCompany = Company(
            id = 1L,
            name = "Test Company",
            country = "CO",
            companyIdentification = "1234567890",
            users = null
        )
        mockUser = User(
            id = 1L,
            company = mockCompany,
            username = "user",
            password = null,
            email = "test@test.com",
            name = "john doe",
            phone = "123-456-7890",
            country = "CO",
            enabled = true,
            lastModified = null,
            createdDate = null,
            roles = null,
            companyId = 1L
        )
        mockModulesAction = ModulesAction(
            id = 1L,
            name = "Test Action",
            createdDate = null,
            updatedDate = null,
            moduleId = 1L
        )
        mockPermission = Permission(
            id = 1L,
            name = "Test Permission",
            modulesAction = mutableSetOf(mockModulesAction),
            company = mockCompany
        )
        mockRole = Role(
            id = 1L, name = "Test Role",
            company = mockCompany,
            permissions = mutableSetOf(mockPermission)
        )

        mockModule = Module(
            id = 1L,
            name = "Test Module",
            modulesActionEntities = null
        )
        SecurityContextHolder.setContext(securityContext)
    }

    @Test
    fun `saveRoles - successful permission roles update`() {
        // Given
        val permissionRolesDTO = PermissionRolesDTO(
            id = 1L,
            roles = setOf(RoleDTO(
                id = 1L, name = "Test Role",
                company = null,
                permissions = null
            )),
            name = "test",
            modulesAction = null,
            company = null
        )
        val validRoles = listOf(mockRole)
        val rolesToUpdate = setOf(mockRole)

        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(permissionPersistencePort.findById(permissionRolesDTO.id!!)).thenReturn(mockPermission)
        Mockito.`when`(rolePersistencePort.findAllByCompanyId(mockUser.company!!.id)).thenReturn(validRoles)
        Mockito.`when`(permissionDomainService.updatePermissionRoles(
            mockPermission,
            rolesToUpdate,
            validRoles,
            mockUser
        )).thenReturn(validRoles.toSet())

        // When
        val result = permissionService.saveRoles(permissionRolesDTO)

        // Then
        assert(result.message == Responses.PERMISSION_UPDATED.format(mockPermission.name))
        Mockito.verify(rolePersistencePort).saveAll(validRoles)
    }

    @Test
    fun `saveModules - successful permission modules update`() {
        // Given
        val moduleActionDTO = ModulesActionDTO(
            id = 1L,
            name = "Test Action",
            module = null
        )
        val permissionModulesDTO = PermissionModulesDTO(
            id = 1L,
            moduleId = 1L,
            modulesAction = setOf(moduleActionDTO),
            name = "test"
        )
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(permissionPersistencePort.findById(permissionModulesDTO.id)).thenReturn(mockPermission)
        Mockito.`when`(modulesActionPersistencePort.getAllByIdIn(listOf(1L))).thenReturn(listOf(mockModulesAction))
        Mockito.`when`(modulesActionMapper.toDomain(moduleActionDTO)).thenReturn(mockModulesAction)

        // When
        val result = permissionService.saveModules(permissionModulesDTO)

        // Then
        assert(result.message == Responses.PERMISSION_UPDATED.format(mockPermission.name))
        Mockito.verify(permissionPersistencePort).save(mockPermission)
    }

    @Test
    fun `save - successful permission creation`() {
        // Given
        val companyDTO = CompanyDTO(
            id = 1L,
            name = "Test Company",
            country = "CO",
            companyIdentification = "1234567890"
        )
        val permissionDTO = PermissionDTO(
            name = "Test Permission",
            company = companyDTO,
            id = null,
            modulesAction = null,
            role = null
        )

        Mockito.`when`(permissionPersistencePort.findAllByCompanyId(companyDTO.id!!))
            .thenReturn(emptySet())
        Mockito.`when`(permissionMapper.toDomain(permissionDTO)).thenReturn(mockPermission)

        // When
        val result = permissionService.save(permissionDTO)

        // Then
        assert(result.message == Responses.PERMISSION_CREATED.format(permissionDTO.name))
        Mockito.verify(permissionPersistencePort).save(mockPermission)
    }

    @Test
    fun `update - successful permission update`() {
        // Given
        val permissionDTO = PermissionDTO(
            id = 1L,
            name = "Updated Permission",
            modulesAction = null,
            role = null,
            company = null
        )
        val companyDTO = CompanyDTO(
            id = 1L,
            name = "Test Company",
            country = "CO",
            companyIdentification = "1234567890"
        )

        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(permissionPersistencePort.findAllByCompanyId(mockUser.company!!.id))
            .thenReturn(emptySet())
        Mockito.`when`(companyMapper.toDTO(mockUser.company!!)).thenReturn(companyDTO)
        Mockito.`when`(permissionMapper.toDomain(permissionDTO)).thenReturn(mockPermission)

        // When
        val result = permissionService.update(permissionDTO)

        // Then
        assert(result.message == Responses.PERMISSION_UPDATED.format(permissionDTO.name))
        Mockito.verify(permissionPersistencePort).update(mockPermission)
    }

    @Test
    fun `getAllByFilterPaginated - returns paginated results`() {
        // Given
        val filterDTO = PermissionDTO(
            id = null,
            name = null,
            modulesAction = null,
            role = null,
            company = null
        )
        val pageable = PageRequest.of(0, 10)
        val mockFilter = Permission(
            id = null,
            name = null,
            modulesAction = null,
            company = null
        )
        val mockPage: Page<Permission> = PageImpl(listOf(mockPermission))
        val mockPermissionDTO = PermissionDTO(
            id = 1L, name = "Test Permission",
            modulesAction = null,
            role = null,
            company = null
        )

        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(permissionMapper.toDomain(filterDTO)).thenReturn(mockFilter)
        Mockito.`when`(permissionPersistencePort.findAllByFilterPaginated(mockFilter, pageable, mockUser.company!!.id))
            .thenReturn(mockPage)
        Mockito.`when`(permissionMapper.toDTO(mockPermission)).thenReturn(mockPermissionDTO)

        // When
        val result = permissionService.getAllByFilterPaginated(filterDTO, pageable)

        // Then
        assert(result.content.size == 1)
        assert(result.content.first().id == mockPermissionDTO.id)
    }

    @Test
    fun `delete - successful permission deletion`() {
        // Given
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(permissionPersistencePort.findById(1L)).thenReturn(mockPermission)
        Mockito.doNothing().`when`(permissionDomainService).validateDeletion(mockUser, mockPermission)

        // When
        val result = permissionService.delete(1L)

        // Then
        assert(result?.message == Responses.PERMISSION_DELETED)
        Mockito.verify(permissionPersistencePort).delete(1L)
    }

    @Test
    fun `hasPermission - returns true when user has permission`() {
        // Given
        val moduleName = "Test Module"
        val actionName = "Test Action"
        mockModule.modulesActionEntities = mutableListOf(mockModulesAction)
        mockModulesAction.name = actionName
        mockRole.permissions = mutableSetOf(mockPermission)
        mockPermission.modulesAction = mutableSetOf(mockModulesAction)
        mockUser.roles = mutableSetOf(mockRole)

        Mockito.`when`(modulePersistencePort.findByName(moduleName)).thenReturn(mockModule)

        // When
        val result = permissionService.hasPermission(mockUser, moduleName, actionName)

        // Then
        assert(result)
    }

    @Test
    fun `validatePermission - returns success response when user has permission`() {
        // Given
        val userDetailsDTO = UserDetailsDTO(
            id = 1L,
            companyId = 1L,
            username = "user",
            name = "john doe",
            email = "test@test.com",
            phone = "123-456-7890",
            country = "CO",
            enabled = false,
            roles = null,
            moduleActions = null,
            actions = null
        )
        val moduleName = "Test Module"
        val actionName = "Test Action"

        Mockito.`when`(userMapper.toDomain(userDetailsDTO)).thenReturn(mockUser)
        mockModule.modulesActionEntities = mutableListOf(mockModulesAction)
        mockModulesAction.name = actionName
        mockRole.permissions = mutableSetOf(mockPermission)
        mockPermission.modulesAction = mutableSetOf(mockModulesAction)
        mockUser.roles = mutableSetOf(mockRole)
        Mockito.`when`(modulePersistencePort.findByName(moduleName)).thenReturn(mockModule)

        // When
        val result = permissionService.validatePermission(userDetailsDTO, moduleName, actionName)

        // Then
        assert(result?.message == userDetailsDTO.companyId.toString())
    }

}