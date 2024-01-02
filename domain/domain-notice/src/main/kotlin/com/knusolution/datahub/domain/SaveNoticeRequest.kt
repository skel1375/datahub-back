package com.knusolution.datahub.domain

data class SaveNoticeRequest(
        val loginId: String,
        val noticeTitle: String,
        val noticeContent: String
)
