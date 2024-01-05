package com.knusolution.datahub.domain

data class ReplyResponse(
    val qa:QAInfoDto,
    val content:String,
    val replys:List<ReplyInfoDto>
)
