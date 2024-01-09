package com.knusolution.datahub.system.domain

import com.knusolution.datahub.system.domain.BaseCategoryDto
import com.knusolution.datahub.system.domain.BaseCategoryEntity

data class BaseCategoryResponse(
    val systemName:String,
    val baseCategories : List<BaseCategoryDto>,
)
