package com.petcaresuite.management.interfaces.rest.authentication

import com.petcaresuite.management.domain.model.AuthenticationRequest
import com.petcaresuite.management.domain.model.AuthenticationResponse
import com.petcaresuite.management.domain.model.RegisterDTO
import com.petcaresuite.management.domain.service.JwtAuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/authentication")
class AuthenticationController(private val authService: JwtAuthenticationService) {

    @PostMapping("/register")
    fun register(@RequestBody dto: RegisterDTO): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authService.register(dto))
    }

    @PostMapping("/login")
    fun authenticate(@RequestBody authRequest: AuthenticationRequest): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authService.authenticate(authRequest))
    }
}