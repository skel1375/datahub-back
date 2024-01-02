package com.knusolution.datahub.domain

data class QAInfoResponse(
    val allPage:Int,
    val page:Int,
    val qas:List<QAInfoDto>
)
