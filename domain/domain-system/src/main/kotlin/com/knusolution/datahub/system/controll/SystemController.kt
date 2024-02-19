package com.knusolution.datahub.system.controll

import com.knusolution.datahub.system.application.SystemService
import com.knusolution.datahub.system.domain.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemController(
    private val systemService: SystemService,
    private val systemRepository: SystemRepository,
) {
    //기본카테고리 정보 전달
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/base-category")
    fun getBaseCategory(@RequestParam systemId:Long): BaseCategoryResponse?
    {
        if(!systemService.existsDbSystem(systemId)) return null
        val system = systemService.getDbSystem(systemId)
        val baseCategories = systemService.getBaseCategories(systemId).map{it.asDto()}
        return BaseCategoryResponse(systemName = system.systemName, baseCategories = baseCategories)
    }
    //세부카테고리 정보 전달
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/detail-category")
    fun getDetailCategory(@RequestParam baseCategoryId:Long): DetailCategoryResponse?
    {
        if(!systemService.existsBaseCategory(id = baseCategoryId)) return null
        val detailCategories = systemService.getDetailCategories(id = baseCategoryId).map{it.asDto()}
        return DetailCategoryResponse(detailCategories = detailCategories)
    }
    //사이드바시스템 정보전달
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/side")
    fun getSystemList(@RequestParam systemId: Long):SystemResponse{
        return SystemResponse(systemService.getSide(systemId).map{it.asSystemInfo()})
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/organization")
    fun getOrganization(@RequestParam systemId: Long):SystemPageResponse
    {
        return systemService.getOrganization(systemId)
    }

    //산출물추가
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/detail-category/post")
    fun addDetailCategory(
        @RequestParam baseCategoryId: Long,
        @RequestParam category:String
    )
    {
        systemService.addOutputType(baseCategoryId,category)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/detail-category/del")
    fun delDetailCategory(
        @RequestParam detailCategoryId:Long
    )
    {
        systemService.delOutputType(detailCategoryId)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("detail-category/update")
    fun updateDetailCategory(
        @RequestParam detailCategoryId: Long,
        @RequestParam category: String
    )
    {
        systemService.updateOutputType(detailCategoryId,category)
    }
}