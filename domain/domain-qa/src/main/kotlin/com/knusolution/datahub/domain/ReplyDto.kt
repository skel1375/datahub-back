package com.knusolution.datahub.domain

class ReplyDto(
    val replyDate:String,
    val replyContent:String,
    val user:UserEntity,
    val qa:QAEntity
)

fun ReplyEntity.asDto()=ReplyDto(
    replyDate=this.replyDate,
    replyContent=this.replyContent,
    user = this.user,
    qa = this.qa
)