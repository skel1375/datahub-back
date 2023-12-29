package com.knusolution.datahub.domain

data class NoticeInfoResponse(
        val allPage:Int,
        val page:Int,
        val notices: List<NoticeInfoDto>
)
