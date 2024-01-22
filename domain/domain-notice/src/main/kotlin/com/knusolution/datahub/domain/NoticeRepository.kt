package com.knusolution.datahub.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository : JpaRepository<NoticeEntity, Long> {
    fun findByUser(userId : UserEntity): List<NoticeEntity>
    fun findByNoticeTitleContaining(keyword: String, pageable: Pageable): Page<NoticeEntity>
    fun findByNoticeContentContaining(keyword: String, pageable: Pageable): Page<NoticeEntity>
}