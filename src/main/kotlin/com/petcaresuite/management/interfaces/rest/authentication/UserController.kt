package com.petcaresuite.management.interfaces.rest.authentication

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController {

    @GetMapping("/welcome")
    fun welcome(): String {
        return "Welcome page"
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun userProfile(): String {
        return "User profile is shown here."
    }
}