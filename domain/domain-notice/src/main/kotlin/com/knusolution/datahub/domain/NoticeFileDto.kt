package com.knusolution.datahub.domain

data class NoticeFileDto(
    val fileUrl : String,
    val fileName : String,
    val notice : NoticeEntity
)

fun NoticeFileEntity.asDto() = NoticeFileDto(
    fileUrl=this.fileUrl,
    fileName=this.fileName,
    notice = this.notice
)
