package com.knusolution.datahub.domain

import org.springframework.data.jpa.repository.JpaRepository

interface NoticeFileRepository : JpaRepository<NoticeFileEntity, Long>{
    fun findByNotice(notice: NoticeEntity) : List<NoticeFileEntity>?
    fun findByFileId(fileId:Long):NoticeFileEntity
}