package com.knusolution.datahub.domain

import org.apache.catalina.User
import org.springframework.data.annotation.Id

data class QAInfoDto(
    val qaId: Long,
    val qaTitle:String,
    val qaDate:String,
    val username:String
)

fun QAEntity.asInfoDto() = QAInfoDto(
    qaId = this.qaId,
    qaTitle=this.qaTitle,
    qaDate=this.qaDate,
    username = this.userId.loginId
)
