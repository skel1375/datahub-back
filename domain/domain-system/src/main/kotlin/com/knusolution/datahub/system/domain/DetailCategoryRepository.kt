package com.knusolution.datahub.system.domain

import org.springframework.data.jpa.repository.JpaRepository

interface DetailCategoryRepository:JpaRepository<DetailCategoryEntity,Long> {
    fun findByDetailCategoryId(detailCategoryId:Long):DetailCategoryEntity
    fun findAllByBaseCategoryBaseCategoryId(baseCategoryId:Long):List<DetailCategoryEntity>
    fun findAllByBaseCategoryBaseCategoryIdIn(baseCategoryIds:List<Long>):List<DetailCategoryEntity>
}