package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.application.port.input.UserUseCase
import com.petcaresuite.management.application.security.Authorize
import com.petcaresuite.management.application.service.modules.ModuleActions
import com.petcaresuite.management.application.service.modules.Modules
import com.petcaresuite.management.infrastructure.security.PermissionRequired

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Validated
class UserController(private val userUseCase: UserUseCase) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody dto: UserRegisterDTO): ResponseEntity<AuthenticationResponseDTO> {
        return ResponseEntity.ok(userUseCase.register(dto))
    }

    @PutMapping
    @Authorize
    fun update(@Valid @RequestBody dto: UserUpdateDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(userUseCase.update(dto))
    }

    @GetMapping("/token")
    @Authorize
    fun getUserData(@RequestParam token: String): ResponseEntity<UserDetailsDTO> {
        return ResponseEntity.ok(userUseCase.getByToken(token))
    }

    @GetMapping("/identificationTypes")
    @PermissionRequired(Modules.OWNER, ModuleActions.VIEW)
    fun getIdentificationTypes(): ResponseEntity<List<IdentificationTypeDTO>> {
        return ResponseEntity.ok(userUseCase.getIdentificationTypes())
    }

}