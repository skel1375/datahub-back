package com.knusolution.datahub.domain

import com.knusolution.datahub.domain.ArticleDto
import com.knusolution.datahub.domain.ArticleInfoDto

data class ArticleResponse(
    val allPage:Int,
    val page:Int,
    val articles: List<ArticleInfoDto>,
)