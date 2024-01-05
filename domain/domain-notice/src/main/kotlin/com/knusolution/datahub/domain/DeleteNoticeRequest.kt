package com.knusolution.datahub.domain

data class DeleteNoticeRequest(
        val loginId: String,
        val noticeId: Long
)
