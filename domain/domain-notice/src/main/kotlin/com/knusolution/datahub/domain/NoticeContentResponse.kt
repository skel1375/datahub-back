package com.knusolution.datahub.domain

data class NoticeContentResponse(
        val noticeTitle: String,
        val noticeContent: String,
        val noticeFile: List<NoticeFileInfoDto>?
)
