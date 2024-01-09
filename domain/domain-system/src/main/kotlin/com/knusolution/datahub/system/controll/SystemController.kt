package com.knusolution.datahub.system.controll

import com.knusolution.datahub.system.domain.BaseCategoryResponse
import com.knusolution.datahub.system.domain.DetailCategoryResponse
import com.knusolution.datahub.system.application.SystemService
import com.knusolution.datahub.system.domain.SystemInfo
import com.knusolution.datahub.system.domain.asDto
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemController(
    private val systemService: SystemService,
) {
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/base-category")
    fun getBaseCategory(@RequestParam systemId:Long): BaseCategoryResponse?
    {
        if(!systemService.existsDbSystem(systemId)) return null
        val system = systemService.getDbSystem(systemId)
        val baseCategories = systemService.getBaseCategories(systemId).map{it.asDto()}
        return BaseCategoryResponse(systemName = system.systemName, baseCategories = baseCategories)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/detail-category")
    fun getDetailCategory(@RequestParam baseCategoryId:Long): DetailCategoryResponse?
    {
        if(!systemService.existsBaseCategory(id = baseCategoryId)) return null
        val detailCategories = systemService.getDetailCategories(id = baseCategoryId).map{it.asDto()}
        return DetailCategoryResponse(detailCategories = detailCategories)
    }

    @GetMapping("/system")
    fun getSystemList():List<SystemInfo>{
        return systemService.getAllSystem()
    }
}