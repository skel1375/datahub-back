package com.knusolution.datahub.domain

data class ArticleInfoDto(
    val articleId:Long,
    val uploadDate:String,
    val approval:String,
    val declineDetail:String,
    val taskFileUrl:String,
    val taskFileName:String,
    val declineFileUrl:String,
    val declineFileName:String,
)

fun ArticleEntity.asInfoDto() = ArticleInfoDto(
    articleId = this.articleId,
    uploadDate = this.uploadDate,
    approval = this.approval,
    declineDetail = this.declineDetail,
    taskFileUrl = this.taskFileUrl,
    taskFileName = this.taskFileName,
    declineFileUrl = this.declineFileUrl,
    declineFileName = this.declineFileName
)