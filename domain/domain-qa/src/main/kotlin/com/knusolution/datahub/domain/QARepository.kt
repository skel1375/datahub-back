package com.knusolution.datahub.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface QARepository: JpaRepository<QAEntity,Long> {
    fun findByUser(userId : UserEntity): List<QAEntity>
    fun findByQaId(qaId: Long):QAEntity

    fun findByQaTitleContaining(keyword: String, pageable: Pageable) : Page<QAEntity>
    fun findByUserUserId(userId: Long, pageable: Pageable): Page<QAEntity>
    fun findByQaContentContaining(keyword: String, pageable: Pageable): Page<QAEntity>
}