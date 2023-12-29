package com.knusolution.datahub.domain

data class NoticeInfoDto(
        val noticeId : Long,
        var noticeTitle: String,
        var noticeDate: String
)

fun NoticeEntity.asInfoDto() = NoticeInfoDto(
        noticeId = this.user.userId,
        noticeTitle = this.noticeTitle,
        noticeDate  = this.noticeDate
)