package com.petcaresuite.management

import com.petcaresuite.management.application.dto.ModulesActionDTO
import com.petcaresuite.management.application.dto.ModuleDTO
import com.petcaresuite.management.application.mapper.ModulesActionMapper
import com.petcaresuite.management.application.port.output.ModulesActionPersistencePort
import com.petcaresuite.management.application.service.ModulesActionService
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.domain.model.Module
import com.petcaresuite.management.domain.model.ModulesAction
import com.petcaresuite.management.domain.service.ModulesActionValidationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ModulesActionServiceTest {

    @Mock
    private lateinit var validationService: ModulesActionValidationService

    @Mock
    private lateinit var modulesActionPersistencePort: ModulesActionPersistencePort

    @Mock
    private lateinit var modulesActionMapper: ModulesActionMapper

    private lateinit var modulesActionService: ModulesActionService
    private lateinit var mockModuleDTO: ModuleDTO
    private lateinit var mockModulesActionDTO: ModulesActionDTO
    private lateinit var mockModule: Module
    private lateinit var mockModulesAction: ModulesAction

    @BeforeEach
    fun setUp() {
        mockModuleDTO = ModuleDTO(
            id = 1L,
            name = "Test Module",
            modulesActionEntities = null,
            selected = false,
        )

        mockModulesActionDTO = ModulesActionDTO(
            id = null,
            name = "Create",
            module = mockModuleDTO
        )

        mockModule = Module(
            id = 1L,
            name = "Test Module",
            modulesActionEntities = null,
        )

        mockModulesAction = ModulesAction(
            id = null,
            name = "Test Action",
            createdDate = null,
            updatedDate = null,
            moduleId = 0L,
        )

        modulesActionService = ModulesActionService(
            validationService,
            modulesActionPersistencePort,
            modulesActionMapper
        )
    }

    @Test
    fun `save - successful module action creation`() {
        // Given
        Mockito.doNothing().`when`(validationService).validateModuleId(mockModuleDTO.id!!)
        Mockito.doNothing().`when`(validationService).validateNameDuplicated(mockModulesActionDTO.name, mockModuleDTO.id!!)
        Mockito.`when`(modulesActionMapper.toDomain(mockModulesActionDTO)).thenReturn(mockModulesAction)

        // When
        val result = modulesActionService.save(mockModulesActionDTO)

        // Then
        Mockito.verify(validationService).validateModuleId(mockModuleDTO.id!!)
        Mockito.verify(validationService).validateNameDuplicated(mockModulesActionDTO.name, mockModuleDTO.id!!)
        Mockito.verify(modulesActionMapper).toDomain(mockModulesActionDTO)
        Mockito.verify(modulesActionPersistencePort).save(mockModulesAction)
        assert(result.message == Responses.MODULES_ACTION_CREATED.format(mockModulesActionDTO.name))
    }

    @Test
    fun `save - throws exception when module id is invalid`() {
        // Given
        val errorMessage = "Invalid module ID"
        Mockito.doThrow(IllegalArgumentException(errorMessage))
            .`when`(validationService).validateModuleId(mockModuleDTO.id!!)

        // When/Then
        val exception = assertThrows<IllegalArgumentException> {
            modulesActionService.save(mockModulesActionDTO)
        }
        assert(exception.message == errorMessage)
        Mockito.verify(modulesActionPersistencePort, Mockito.never()).save(safeAny())
    }

    @Test
    fun `save - throws exception when action name is duplicated`() {
        // Given
        val errorMessage = "Duplicated action name"
        Mockito.doNothing().`when`(validationService).validateModuleId(mockModuleDTO.id!!)
        Mockito.doThrow(IllegalArgumentException(errorMessage))
            .`when`(validationService).validateNameDuplicated(mockModulesActionDTO.name, mockModuleDTO.id!!)

        // When/Then
        val exception = assertThrows<IllegalArgumentException> {
            modulesActionService.save(mockModulesActionDTO)
        }
        assert(exception.message == errorMessage)
        Mockito.verify(modulesActionPersistencePort, Mockito.never()).save(safeAny())
    }

    @Test
    fun `save - throws exception when module is null`() {
        // Given
        val invalidDTO = mockModulesActionDTO.copy(module = null)

        // When/Then
        assertThrows<NullPointerException> {
            modulesActionService.save(invalidDTO)
        }
        Mockito.verify(modulesActionPersistencePort, Mockito.never()).save(safeAny())
    }

    @Test
    fun `save - throws exception when module id is null`() {
        // Given
        val invalidModuleDTO = mockModuleDTO.copy(id = null)
        val invalidDTO = mockModulesActionDTO.copy(module = invalidModuleDTO)

        // When/Then
        assertThrows<NullPointerException> {
            modulesActionService.save(invalidDTO)
        }
        Mockito.verify(modulesActionPersistencePort, Mockito.never()).save(safeAny())
    }

    // Null-safe any() matcher implementation
    private fun <T> safeAny(): T {
        Mockito.any<T>()
        @Suppress("UNCHECKED_CAST")
        return null as T
    }
}