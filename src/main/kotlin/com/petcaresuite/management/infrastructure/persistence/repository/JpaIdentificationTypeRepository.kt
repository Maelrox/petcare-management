package com.petcaresuite.management.infrastructure.persistence.repository

import com.petcaresuite.management.infrastructure.persistence.entity.IdentificationTypeEntity

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository

@Repository
interface JpaIdentificationTypeRepository : JpaRepository<IdentificationTypeEntity, Long> {

}