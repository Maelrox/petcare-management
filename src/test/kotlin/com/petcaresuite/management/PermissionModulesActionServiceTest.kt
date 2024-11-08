package com.petcaresuite.management.application.service

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.mapper.ModulesActionMapper
import com.petcaresuite.management.application.port.output.ModulesActionPersistencePort
import com.petcaresuite.management.application.port.output.PermissionPersistencePort
import com.petcaresuite.management.application.port.output.RolePersistencePort
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Company
import com.petcaresuite.management.domain.model.ModulesAction
import com.petcaresuite.management.domain.model.Permission
import com.petcaresuite.management.domain.model.Role
import com.petcaresuite.management.domain.model.User
import com.petcaresuite.management.domain.service.ModulesActionValidationService
import com.petcaresuite.management.domain.service.PermissionDomainService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PermissionModulesActionServiceTest {

    @Mock
    private lateinit var permissionDomainService: PermissionDomainService

    @Mock
    private lateinit var permissionPersistencePort: PermissionPersistencePort

    @Mock
    private lateinit var moduleActionPersistencePort: ModulesActionPersistencePort

    @Mock
    private lateinit var modulesActionMapper: ModulesActionMapper

    @Mock
    private lateinit var rolePersistencePort: RolePersistencePort

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var modulesActionValidationService: ModulesActionValidationService

    private lateinit var permissionModulesActionService: PermissionModulesActionService

    private lateinit var mockCompany: Company
    private lateinit var mockUser: User
    private lateinit var mockPermission: Permission
    private lateinit var mockModulesAction: ModulesAction
    private lateinit var mockRoles: List<Role>
    private lateinit var mockPermissionDTO: PermissionDTO
    private lateinit var mockModulesActionDTO: ModulesActionDTO
    private lateinit var mockPermissionModuleActionDTO: PermissionModuleActionDTO

    @BeforeEach
    fun setUp() {
        permissionModulesActionService = PermissionModulesActionService(
            permissionDomainService,
            permissionPersistencePort,
            moduleActionPersistencePort,
            modulesActionMapper,
            rolePersistencePort,
            userService,
            modulesActionValidationService
        )

        mockCompany = Company(
            id = 1L, name = "Test Company",
            country = "CO",
            companyIdentification = "1234567890",
            users = emptyList()
        )
        mockUser = User(
            id = 1L, company = mockCompany,
            username = "user",
            password = null,
            email = "test@test.com",
            name = "john doe",
            phone = "123-456-7890",
            country = "CO",
            enabled = true,
            lastModified = null,
            createdDate = null,
            roles = emptySet(),
            companyId = 1L
        )
        mockModulesAction = ModulesAction(
            id = 1L, name = "Test Action",
            moduleId = 1L,
            createdDate = null,
            updatedDate = null
        )
        mockPermission = Permission(
            id = 1L, name = "Test Permission",
            modulesAction = mutableSetOf(mockModulesAction),
            company = mockCompany
        )

        mockRoles = listOf(Role(
            id = 1L, name = "Test Role",
            company = mockCompany,
            permissions = mutableSetOf(mockPermission),
        ))

        mockPermissionDTO = PermissionDTO(
            id = 1L,
            name = "Test Permission",
            modulesAction = null,
            role = null,
            company = null
        )

        mockModulesActionDTO = ModulesActionDTO(
            id = 1L,
            name = "Test Action",
            module = ModuleDTO(
                id = 1L, name = "Test Module",
                modulesActionEntities = null,
                selected = true
            )
        )

        mockPermissionModuleActionDTO = PermissionModuleActionDTO(
            permission = mockPermissionDTO,
            moduleAction = mockModulesActionDTO
        )
    }

    @Test
    fun `save - successful permission module action creation`() {
        // Given
        Mockito.`when`(permissionPersistencePort.findById(mockPermissionDTO.id!!)).thenReturn(mockPermission)
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(rolePersistencePort.findAllByCompanyId(mockUser.company?.id!!)).thenReturn(mockRoles)
        Mockito.`when`(modulesActionMapper.toDomain(mockModulesActionDTO)).thenReturn(mockModulesAction)

        // Validation mocks
        Mockito.doNothing().`when`(modulesActionValidationService).validateCompanyRoles(mockRoles)
        Mockito.doNothing().`when`(permissionDomainService).validatePermissionAccess(mockRoles, mockPermission.id!!)
        Mockito.doNothing().`when`(modulesActionValidationService).validateModulesActionId(mockModulesAction.id!!)

        // When
        val result = permissionModulesActionService.save(mockPermissionModuleActionDTO)

        // Then
        Mockito.verify(permissionPersistencePort).findById(mockPermissionDTO.id!!)
        Mockito.verify(userService).getCurrentUser()
        Mockito.verify(rolePersistencePort).findAllByCompanyId(mockUser.company?.id!!)
        Mockito.verify(modulesActionMapper).toDomain(mockModulesActionDTO)
        Mockito.verify(permissionPersistencePort).save(mockPermission)
        assert(result.message == Responses.PERMISSION_MODULES_ACTION_CREATED)
    }

    @Test
    fun `save - throws exception when permission not found`() {
        // Given
        Mockito.`when`(permissionPersistencePort.findById(mockPermissionDTO.id!!))
            .thenThrow(NoSuchElementException("Permission not found"))

        // When/Then
        assertThrows<NoSuchElementException> {
            permissionModulesActionService.save(mockPermissionModuleActionDTO)
        }

        Mockito.verify(permissionPersistencePort, Mockito.never()).save(safeAny())
    }

    @Test
    fun `save - throws exception when company roles validation fails`() {
        // Given
        Mockito.`when`(permissionPersistencePort.findById(mockPermissionDTO.id!!)).thenReturn(mockPermission)
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(rolePersistencePort.findAllByCompanyId(mockUser.company?.id!!)).thenReturn(mockRoles)
        Mockito.`when`(modulesActionMapper.toDomain(mockModulesActionDTO)).thenReturn(mockModulesAction)

        Mockito.doThrow(IllegalArgumentException("Invalid company roles"))
            .`when`(modulesActionValidationService).validateCompanyRoles(mockRoles)

        // When/Then
        assertThrows<IllegalArgumentException> {
            permissionModulesActionService.save(mockPermissionModuleActionDTO)
        }

        Mockito.verify(permissionPersistencePort, Mockito.never()).save(safeAny())
    }

    @Test
    fun `save - throws exception when permission access validation fails`() {
        // Given
        Mockito.`when`(permissionPersistencePort.findById(mockPermissionDTO.id!!)).thenReturn(mockPermission)
        Mockito.`when`(userService.getCurrentUser()).thenReturn(mockUser)
        Mockito.`when`(rolePersistencePort.findAllByCompanyId(mockUser.company?.id!!)).thenReturn(mockRoles)
        Mockito.`when`(modulesActionMapper.toDomain(mockModulesActionDTO)).thenReturn(mockModulesAction)

        Mockito.doNothing().`when`(modulesActionValidationService).validateCompanyRoles(mockRoles)
        Mockito.doThrow(IllegalArgumentException("Invalid permission access"))
            .`when`(permissionDomainService).validatePermissionAccess(mockRoles, mockPermission.id!!)

        // When/Then
        assertThrows<IllegalArgumentException> {
            permissionModulesActionService.save(mockPermissionModuleActionDTO)
        }

        Mockito.verify(permissionPersistencePort, Mockito.never()).save(safeAny())
    }

    @Test
    fun `getAllByPermissionIdAndModuleId - returns mapped DTOs`() {
        // Given
        val permissionId = 1L
        val moduleId = 1L
        val modulesActions = listOf(mockModulesAction)
        val expectedDTOs = listOf(mockModulesActionDTO)

        Mockito.`when`(moduleActionPersistencePort.getByPermissionIdAndModuleId(permissionId, moduleId))
            .thenReturn(modulesActions)
        Mockito.`when`(modulesActionMapper.toDTO(modulesActions)).thenReturn(expectedDTOs)

        // When
        val result = permissionModulesActionService.getAllByPermissionIdAndModuleId(permissionId, moduleId)

        // Then
        assert(result == expectedDTOs)
        Mockito.verify(moduleActionPersistencePort).getByPermissionIdAndModuleId(permissionId, moduleId)
        Mockito.verify(modulesActionMapper).toDTO(modulesActions)
    }

    // Null-safe any() matcher implementation
    private fun <T> safeAny(): T {
        Mockito.any<T>()
        @Suppress("UNCHECKED_CAST")
        return null as T
    }
}