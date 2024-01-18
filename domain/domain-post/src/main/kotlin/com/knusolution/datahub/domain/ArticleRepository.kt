package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.DetailCategoryEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.awt.print.Pageable

interface ArticleRepository: JpaRepository<ArticleEntity,Long> {
    fun findAllByDetailCategory(detailCategoryEntity: DetailCategoryEntity,pageRequest: PageRequest) : Page<ArticleEntity>
    fun findByDetailCategory(detailCategoryEntity: DetailCategoryEntity):List<ArticleEntity>
    fun findAllByApproval(approval: String,pageRequest: PageRequest) : Page<ArticleEntity>
    fun findByArticleId(articleId:Long): ArticleEntity
}