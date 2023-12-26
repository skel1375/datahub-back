package com.knusolution.datahub.system.domain

import org.springframework.data.jpa.repository.JpaRepository

interface BaseCategoryRepository:JpaRepository<BaseCategoryEntity,Long> {
    fun findAllBySystemSystemId(systemId:Long):List<BaseCategoryEntity>
}