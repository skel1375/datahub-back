package com.knusolution.datahub.domain

class ReplyDto(
    val replyDate:String,
    val replyContent:String,
    val userId:UserEntity,
    val qaId:QAEntity
)

fun ReplyEntity.asDto()=ReplyDto(
    replyDate=this.replyDate,
    replyContent=this.replyContent,
    userId = this.userId,
    qaId = this.qaId
)