package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.DetailCategoryEntity
import java.time.LocalDateTime

data class ArticleDto(
    val uploadDate:String,
    val approval:String,
    val declineDetail:String,
    val taskFileUrl:String,
    val taskFileName:String,
    val declineFileUrl:String,
    val declineFileName:String,
    val detailCategory : DetailCategoryEntity
)

fun ArticleEntity.asDto() = ArticleDto(
    uploadDate = this.uploadDate,
    approval = this.approval,
    declineDetail=this.declineDetail,
    taskFileUrl=this.taskFileUrl,
    taskFileName=this.taskFileName,
    declineFileUrl=this.declineFileUrl,
    declineFileName=this.declineFileName,
    detailCategory = this.detailCategory
)
