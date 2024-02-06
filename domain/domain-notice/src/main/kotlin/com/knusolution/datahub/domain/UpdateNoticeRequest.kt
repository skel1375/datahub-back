package com.knusolution.datahub.domain

data class UpdateNoticeRequest(
        val loginId: String,
        val noticeId: Long,
        val noticeTitle: String,
        val noticeContent: String,
        val delFileIds: List<Long>?
)