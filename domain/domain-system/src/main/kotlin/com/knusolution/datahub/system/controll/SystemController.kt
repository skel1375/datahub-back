package com.knusolution.datahub.system.controll

import com.knusolution.datahub.system.application.SystemService
import com.knusolution.datahub.system.domain.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemController(
    private val systemService: SystemService,
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
    //시스템리스트 전달
    @GetMapping("/system")
    fun getSystemList():SystemResponse{
        val systems = systemService.getAllSystem().map { it.asSystemInfo() }
        return SystemResponse(systems = systems)
    }
}