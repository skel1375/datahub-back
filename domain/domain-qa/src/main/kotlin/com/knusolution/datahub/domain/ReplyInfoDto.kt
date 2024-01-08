package com.knusolution.datahub.domain

data class ReplyInfoDto(
    val replyId: Long,
    val replyDate:String,
    val replyContent:String,
    val username: String
)

fun ReplyEntity.asInfoDto() = ReplyInfoDto(
    replyId=this.replyId,
    replyDate=this.replyDate,
    replyContent=this.replyContent,
    username = this.user.loginId
)
