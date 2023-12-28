package com.knusolution.datahub.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ReplyRepository: JpaRepository<ReplyEntity,Long> {
    fun findByQaId(qaEntity: QAEntity): List<ReplyEntity>
}