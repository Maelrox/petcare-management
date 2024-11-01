package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*
import com.petcaresuite.management.domain.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EmployeeUseCase {
    
    fun register(employeeRegisterDTO: EmployeeRegisterDTO): ResponseDTO

    fun update(employeeRegisterDTO: EmployeeRegisterDTO): ResponseDTO

    fun getByUserName(username: String): User

    fun getAllByFilter(filterDTO: EmployeeFilterDTO, pageable: Pageable): Page<UserDetailsDTO>

}