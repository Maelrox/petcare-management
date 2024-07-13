package com.petcaresuite.management.interfaces.web

import com.petcaresuite.management.application.dto.AuthenticationRequestDTO
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.security.AuthenticationUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authentication")
class AuthenticationController(private val authenticationService: AuthenticationUseCase) {

    @PostMapping("/login")
    fun authenticate(@RequestBody authRequest: AuthenticationRequestDTO): ResponseEntity<AuthenticationResponseDTO> {
        return ResponseEntity.ok(authenticationService.authenticate(authRequest))
    }

}