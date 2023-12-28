package com.knusolution.datahub.domain

data class NoticeDto(
        val noticeId: Long,
        val userId: UserEntity,
        val noticeDate: String,
        val noticeTitle: String,
        val noticeContent: String
)

fun NoticeEntity.asDto() = NoticeDto(
        noticeId = this.noticeId,
        userId = this.userId,
        noticeDate = this.noticeDate,
        noticeTitle = this.noticeTitle,
        noticeContent = this.noticeContent
)
