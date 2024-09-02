package com.petcaresuite.management.application.port.input

import com.petcaresuite.management.application.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RoleUseCase {
    
    fun save(roleDTO: RoleDTO): ResponseDTO

    fun update(roleDTO: RoleDTO): ResponseDTO

    fun getAllByFilter(): List<RoleDTO>?

    fun getAllByFilterPaginated(filterDTO: RoleFilterDTO, pageable: Pageable): Page<RoleDTO>

    fun delete(id: Long): ResponseDTO

}