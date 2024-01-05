package com.knusolution.datahub.domain

data class NoticeDto(
        val user: UserEntity,
        var noticeDate: String,
        var noticeTitle: String,
        var noticeContent: String
)

fun NoticeEntity.asDto() = NoticeDto(
        user = this.user,
        noticeDate = this.noticeDate,
        noticeTitle = this.noticeTitle,
        noticeContent = this.noticeContent
)
