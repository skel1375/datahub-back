package com.knusolution.datahub.domain

data class QADto(
    val qaTitle:String,
    val qaDate:String,
    val qaContent:String,
    val userId:UserEntity
)

fun QAEntity.asDto()=QADto(
    qaTitle=this.qaTitle,
    qaDate=this.qaDate,
    qaContent = this.qaContent,
    userId=this.userId
)