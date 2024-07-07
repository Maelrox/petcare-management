package com.petcaresuite.management.interfaces.web.user

import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.dto.ResponseDTO
import com.petcaresuite.management.application.dto.UserRegisterDTO
import com.petcaresuite.management.application.dto.UserUpdateDTO
import com.petcaresuite.management.application.port.input.IUserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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

    @PutMapping
    fun update(@Valid @RequestBody dto: UserUpdateDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity.ok(userService.update(dto))
    }

}