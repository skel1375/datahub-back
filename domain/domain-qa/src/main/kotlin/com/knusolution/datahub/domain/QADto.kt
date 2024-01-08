package com.knusolution.datahub.domain

data class QADto(
    val qaTitle:String,
    val qaDate:String,
    val qaContent:String,
    val user:UserEntity
)

fun QAEntity.asDto()=QADto(
    qaTitle=this.qaTitle,
    qaDate=this.qaDate,
    qaContent = this.qaContent,
    user=this.user
)