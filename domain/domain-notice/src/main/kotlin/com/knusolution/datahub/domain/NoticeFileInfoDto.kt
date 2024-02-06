package com.knusolution.datahub.domain

data class NoticeFileInfoDto(
    val fileId: Long,
    val fileUrl : String,
    val fileName : String,
)

fun NoticeFileEntity.asInfoDto()= NoticeFileInfoDto(
    fileId = this.fileId,
    fileUrl=this.fileUrl,
    fileName=this.fileName
)