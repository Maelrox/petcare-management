package com.petcaresuite.management.application.port.output

import com.petcaresuite.management.domain.model.Module

interface ModulePersistencePort {

    fun findByName(name: String): Module?

    fun save(module: Module): Module?

    fun existsByName(name: String): Boolean

    fun existsById(id: Long): Boolean

    fun getAll(): List<Module>

    fun findAllById(modulesActionIds: List<Long>): List<Module>

}