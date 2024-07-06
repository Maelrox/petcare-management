package com.petcaresuite.management.interfaces.rest.authentication

import com.petcaresuite.management.application.dto.AuthenticationRequestDTO
import com.petcaresuite.management.application.dto.AuthenticationResponseDTO
import com.petcaresuite.management.application.service.IAuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/authentication")
class AuthenticationController(private val authenticationService: IAuthenticationService) {

    @PostMapping("/login")
    fun authenticate(@RequestBody authRequest: AuthenticationRequestDTO): ResponseEntity<AuthenticationResponseDTO> {
        return ResponseEntity.ok(authenticationService.authenticate(authRequest))
    }

}