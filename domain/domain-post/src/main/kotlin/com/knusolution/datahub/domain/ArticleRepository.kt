package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.DetailCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository: JpaRepository<ArticleEntity,Long> {
    fun findByDetailCategory(detailCategoryEntity: DetailCategoryEntity) : List<ArticleEntity>
    fun findByApproval(approval: String) : List<ArticleEntity>
    fun findByArticleId(articleId:Long): ArticleEntity
    fun findByDetailCategoryDetailCategoryIdIn(detailCategoryIds:List<Long>):List<ArticleEntity>
}