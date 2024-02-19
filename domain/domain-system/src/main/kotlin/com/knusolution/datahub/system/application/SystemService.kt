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

    //사이드바 정보 전달
    private fun findAllChildSystems(systemId:Long): List<SystemEntity> {
        val parentSystem = systemRepository.findBySystemId(systemId)
        val allChildSystems = mutableListOf<SystemEntity>()
        val childSystems = systemRepository.findByParentSystem(parentSystem)
            for (childSystem in childSystems) {
                allChildSystems.add(childSystem)
                allChildSystems.addAll(findAllChildSystems(childSystem.systemId))
            }

        return allChildSystems
    }
    fun getSide(systemId: Long):List<SystemEntity>
    {
        val system = systemRepository.findBySystemId(systemId) // 시스템 ID를 사용하여 시스템을 찾음
        val allChildSystems = mutableListOf<SystemEntity>()

        allChildSystems.add(system) // 시스템을 리스트에 추가
        allChildSystems.addAll(findAllChildSystems(system.systemId)) // 하위 시스템 추가

        return allChildSystems
    }

    //기관정보 전달
    fun getOrganization(systemId:Long):SystemPageResponse
    {
        val parentSystem = systemRepository.findBySystemId(systemId)
        val organization = systemRepository.findByParentSystemAndSystem(parentSystem,false)?.map{it.asSystemInfo()}
        val system = systemRepository.findByParentSystemAndSystem(parentSystem,true)?.map{it.asSystemInfo()}

        return SystemPageResponse(organization,system)
    }

    fun addOutputType(baseCategoryId :Long,output : String)
    {
        val baseCategory = baseCategoryRepository.findByBaseCategoryId(baseCategoryId)
        if(baseCategory.baseCategoryName != "그외 보고서")
            throw(IllegalArgumentException("베이스카테고리를 잘못입력했습니다."))
        val detailCategory = DetailCategoryDto(detailCategoryName = output).asEntity(baseCategory)
        detailCategoryRepository.save(detailCategory)
    }

    fun delOutputType(detailCategoryId: Long)
    {
        detailCategoryRepository.deleteByDetailCategoryId(detailCategoryId)
    }

    fun updateOutputType(detailCategoryId: Long, output:String)
    {
        val detailCategory = detailCategoryRepository.findByDetailCategoryId(detailCategoryId)
        detailCategory.detailCategoryName=output
        detailCategoryRepository.save(detailCategory)
    }
}