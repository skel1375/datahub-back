package com.knusolution.datahub.domain

data class NoticeResponse(
        val allPage:Int,
        val page:Int,
        val notices: List<NoticeInfoDto>
)
