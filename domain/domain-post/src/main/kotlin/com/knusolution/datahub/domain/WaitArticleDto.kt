package com.knusolution.datahub.domain

data class WaitArticleDto(
    val systemName:String,
    val detailCategoryName:String,
    val articleId:Long,
    val approval:String,
    val uploadDate: String,
    val taskFileUrl:String,
    val taskFileName: String,
)

fun ArticleEntity.asWaitDto(systemName: String,detailCategoryName: String) = WaitArticleDto(
    systemName = systemName,
    detailCategoryName = detailCategoryName,
    articleId = this.articleId,
    approval = this.approval,
    uploadDate = this.uploadDate,
    taskFileUrl = this.taskFileUrl,
    taskFileName = this.taskFileName
)