package com.petcaresuite.management.interfaces.rest.user

import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.dto.UserRegisterDTO
import com.petcaresuite.management.application.service.IUserService
import com.petcaresuite.management.domain.service.AuthenticationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Validated
class UserController(private val userService: IUserService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody dto: UserRegisterDTO): ResponseEntity<AuthenticationResponseDTO> {
        return ResponseEntity.ok(userService.register(dto))
    }

}