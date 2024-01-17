package com.knusolution.datahub.domain

import org.springframework.data.jpa.repository.JpaRepository

interface QARepository: JpaRepository<QAEntity,Long> {
    fun findByUser(userId : UserEntity): List<QAEntity>
    fun findByQaId(qaId: Long):QAEntity
}