package com.knusolution.datahub.domain

import org.apache.catalina.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository : JpaRepository<NoticeEntity, Long> {
    fun findByUser(user : UserEntity): NoticeEntity
    fun findByNoticeTitleContaining(keyword: String, pageable: Pageable): Page<NoticeEntity>
    fun findByNoticeContentContaining(keyword: String, pageable: Pageable): Page<NoticeEntity>
}