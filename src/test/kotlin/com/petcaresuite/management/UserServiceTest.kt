import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.exception.BadCredentialsException
import com.petcaresuite.management.application.mapper.CompanyMapper
import com.petcaresuite.management.application.mapper.UserMapper
import com.petcaresuite.management.application.port.output.*
import com.petcaresuite.management.application.service.UserService
import com.petcaresuite.management.application.service.messages.Responses
import com.petcaresuite.management.application.service.modules.ModuleActions
import com.petcaresuite.management.domain.model.*
import com.petcaresuite.management.domain.service.CompanyDomainService
import com.petcaresuite.management.domain.service.RoleDomainService
import com.petcaresuite.management.domain.service.UserDomainService
import com.petcaresuite.management.infrastructure.security.UserDetailsService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var userDomainService: UserDomainService

    @Mock
    private lateinit var rolePersistencePort: RolePersistencePort

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    @Mock
    private lateinit var moduleActionPersistencePort: ModulesActionPersistencePort

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var userMapper: UserMapper

    @Mock
    private lateinit var jwtPort: JwtPort

    @Mock
    private lateinit var userDetailsService: UserDetailsService

    @Mock
    private lateinit var companyDomainService: CompanyDomainService

    @Mock
    private lateinit var companyPersistencePort: CompanyPersistencePort

    @Mock
    private lateinit var permissionPersistencePort: PermissionPersistencePort

    @Mock
    private lateinit var roleDomainService: RoleDomainService

    @Mock
    private lateinit var companyMapper: CompanyMapper

    private lateinit var mockUserRegisterDTO: UserRegisterDTO
    private lateinit var mockUserDetailsDTO: UserDetailsDTO
    private lateinit var mockUser: User
    private lateinit var mockUserDetails: UserDetails
    private lateinit var mockAdminPermission: Permission
    private lateinit var mockAdminRole: Role
    private lateinit var mockCompany: Company
    private lateinit var moduleActionsAdmin: ModulesAction

    private lateinit var mockAllModuleActions: MutableSet<ModulesAction>

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockUserDetails = mock(UserDetails::class.java)
        // Setting up common data for tests
        mockUserRegisterDTO = UserRegisterDTO(
            userName = "testuser",
            password = "password",
            roles = setOf("ADMIN"),
            company = CompanyDTO(
                name = "TestCompany",
                id = null,
                country = "CO",
                companyIdentification = "1234567890"
            ),
            email = "test@test.com",
            name = "john doe",
            phone = "1234567890",
            country = "CO",
            enabled = null,
            companyId = null
        )
        mockCompany = Company(
            id = 0L,
            name = "New Company",
            country = "CO",
            companyIdentification = "1234567890",
            users = null
        )
        mockUser = User(
            username = mockUserRegisterDTO.userName,
            id = 1L,
            password = null,
            email = "test@test.com",
            name = "john doe",
            phone = "01234567890",
            country = "CO",
            enabled = true,
            lastModified = null,
            createdDate = null,
            company = mockCompany,
            roles = emptySet(),
            companyId = 0L
        )
        mockAdminPermission = Permission(
            id = 1L,
            name = "ADMIN",
            modulesAction = null,
            company = null
        )
        userService = UserService(
            userDomainService = userDomainService,
            rolePersistencePort = rolePersistencePort,
            userPersistencePort = userPersistencePort,
            moduleActionPersistencePort = moduleActionPersistencePort,
            passwordEncoder = passwordEncoder,
            userMapper = userMapper,
            jwtPort = jwtPort,
            userDetailsService = userDetailsService,
            companyDomainService = companyDomainService,
            companyPersistencePort = companyPersistencePort,
            permissionPersistencePort = permissionPersistencePort,
            roleDomainService = roleDomainService,
            companyMapper = companyMapper
        )
        moduleActionsAdmin = ModulesAction(
            id = 1L,
            name = "View",
            createdDate = null,
            updatedDate = null,
            moduleId = 1L
        )
        mockAllModuleActions = mutableSetOf(moduleActionsAdmin)
        mockAdminRole = Role(
            id = 1L,
            name = "Admin",
            company = mockCompany,
            permissions = mutableSetOf(mockAdminPermission)
        )
        mockUserDetailsDTO = UserDetailsDTO(
            id = 1L,
            username = "username",
            name = "john doe",
            email = "test@test.com",
            phone = "123-456-7890",
            country = "CO",
            enabled = true,
            companyId = 1L,
            roles = null,
            moduleActions = null,
            actions = null
        )
    }

    @Test
    fun `should register a new user successfully`() {
        `when`(passwordEncoder.encode(mockUserRegisterDTO.password)).thenReturn("encodedPassword")
        `when`(userMapper.toDomain(mockUserRegisterDTO, emptySet())).thenReturn(mockUser)
        `when`(userPersistencePort.save(mockUser)).thenReturn(mockUser)
        `when`(companyMapper.toDomain(mockUserRegisterDTO.company)).thenReturn(mockCompany)
        `when`(companyPersistencePort.save(mockCompany)).thenReturn(mockCompany)
        `when`(moduleActionPersistencePort.getAll()).thenReturn(mockAllModuleActions)
        `when`(roleDomainService.getDefaultPermission(mockUser.company!!, mockAllModuleActions)).thenReturn(mockAdminPermission)
        `when`(permissionPersistencePort.save(mockAdminPermission)).thenReturn(mockAdminPermission)
        `when`(roleDomainService.getDefaultRole(mockUser.company!!, mockAllModuleActions, mutableSetOf(mockAdminPermission))).thenReturn(mockAdminRole)
        `when`(rolePersistencePort.save(mockAdminRole)).thenReturn(mockAdminRole)
        `when`(userMapper.toDTO(mockUser)).thenReturn(mockUserDetailsDTO)

        `when`(jwtPort.generateToken(mockUser.username!!)).thenReturn(Pair("token", Date()))

        val response = userService.register(mockUserRegisterDTO)

        verify(userPersistencePort, times(2)).save(mockUser)
        assertEquals(Responses.USER_CREATED, response.message)
        assertEquals("token", response.token)
    }

    @Test
    fun `should throw exception for invalid role during registration`() {
        val invalidRoleDTO = mockUserRegisterDTO.copy(roles = setOf("USER"))

        val exception = assertThrows<IllegalArgumentException> {
            userService.register(invalidRoleDTO)
        }

        assertEquals(Responses.REGISTER_INVALID_ROLE, exception.message)
    }

    @Test
    fun `should update user successfully`() {
        val userUpdateDTO = UserUpdateDTO(
            id = 1L,
            password = "newPassword",
            roles = setOf("ADMIN"),
            country = "Country",
            enabled = true,
            name = "john doe",
            phone = "1234567890",
            companyId = 1L
        )

        `when`(userPersistencePort.getById(userUpdateDTO.id!!)).thenReturn(mockUser)
        `when`(passwordEncoder.encode(userUpdateDTO.password)).thenReturn("encodedPassword")
        `when`(userPersistencePort.save(mockUser)).thenReturn(mockUser)

        val response = userService.update(userUpdateDTO)

        verify(userPersistencePort).save(mockUser)
        assertEquals(Responses.USER_UPDATED, response.message)
    }

    @Test
    fun `should retrieve user by username`() {
        val username = "testuser"

        `when`(userPersistencePort.getUserInfoByUsername(username)).thenReturn(Optional.of(mockUser))

        val result = userService.getByUserName(username)

        assertEquals(mockUser, result)
    }

    @Test
    fun `should throw exception when retrieving user by invalid username`() {
        val username = "invaliduser"

        `when`(userPersistencePort.getUserInfoByUsername(username)).thenReturn(Optional.empty())

        val exception = assertThrows<IllegalArgumentException> {
            userService.getByUserName(username)
        }

        assertEquals(Responses.USER_NOT_VALID, exception.message)
    }

    @Test
    fun `should retrieve user by token`() {
        val token = "token"
        val username = "testuser"
        val userDetailsDTO = UserDetailsDTO(
            username = username,
            id = 1L,
            name = "john doe",
            email = "test@test.com",
            phone = "1234567890",
            country = "CO",
            enabled = true,
            companyId = 1L,
            roles = setOf(RoleDTO(
                id = 1L,
                name = "ADMIN",
                company = null,
                permissions = null
            )),
            moduleActions = setOf(),
            actions = setOf()
        )

        `when`(jwtPort.extractUsername(token)).thenReturn(username)
        `when`(userDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails)
        `when`(jwtPort.validateToken(token, mockUserDetails)).thenReturn(true)
        `when`(userPersistencePort.getUserInfoByUsername(username)).thenReturn(Optional.of(mockUser))
        `when`(userMapper.toDTO(mockUser)).thenReturn(userDetailsDTO)

        val result = userService.getByToken(token)

        assertEquals(userDetailsDTO, result)
    }

    @Test
    fun `should throw AuthenticationException if token is invalid`() {
        val token = "invalidToken"
        `when`(userDetailsService.loadUserByUsername("username")).thenReturn(mockUserDetails)
        `when`(jwtPort.extractUsername(token)).thenReturn("username")
        `when`(jwtPort.validateToken(token, mockUserDetails)).thenReturn(false)

        assertThrows<BadCredentialsException> {
            userService.getByToken(token)
        }
    }

    private fun <T> customAny(type: Class<T>): T = any<T>(type)

}
