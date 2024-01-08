package com.knusolution.datahub.system.application

import com.knusolution.datahub.system.domain.*
import org.springframework.stereotype.Service

@Service
class SystemService(
    private val systemRepository: SystemRepository,
    private val baseCategoryRepository: BaseCategoryRepository,
    private val detailCategoryRepository: DetailCategoryRepository,
) {
    fun getDbSystem(id:Long) = systemRepository.getReferenceById(id)
    fun existsDbSystem(id:Long) = systemRepository.existsById(id)
    fun getBaseCategories(id:Long) = baseCategoryRepository.findAllBySystemSystemId(systemId = id)

    fun existsBaseCategory(id:Long) = baseCategoryRepository.existsById(id)
    fun getDetailCategories(id:Long) = detailCategoryRepository.findAllByBaseCategoryBaseCategoryId(baseCategoryId = id)

    fun getAllSystem(): List<SystemInfo> {
        return systemRepository.findAll().map { it.asSystemInfo() }
    }
}