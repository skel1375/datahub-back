package com.knusolution.datahub.system.application

import com.knusolution.datahub.system.domain.*
import org.springframework.stereotype.Service

@Service
class SystemService(
    private val systemRepository: SystemRepository,
    private val baseCategoryRepository: BaseCategoryRepository,
    private val detailCategoryRepository: DetailCategoryRepository,
) {
    //시스템 정보
    fun getDbSystem(id:Long) = systemRepository.getReferenceById(id)
    fun existsDbSystem(id:Long) = systemRepository.existsById(id)
    //기반카테고리 정보
    fun getBaseCategories(id:Long) = baseCategoryRepository.findAllBySystemSystemId(systemId = id)
    fun existsBaseCategory(id:Long) = baseCategoryRepository.existsById(id)
    //세부카테고리 정보
    fun getDetailCategories(id:Long) = detailCategoryRepository.findAllByBaseCategoryBaseCategoryId(baseCategoryId = id)
    //모든 시스템정보
    fun getAllSystem() = systemRepository.findAll()
}