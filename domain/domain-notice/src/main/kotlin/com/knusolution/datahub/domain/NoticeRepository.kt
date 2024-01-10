package com.knusolution.datahub.domain

import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository : JpaRepository<NoticeEntity, Long> {
    fun findByUser(userId : UserEntity): List<NoticeEntity>
}