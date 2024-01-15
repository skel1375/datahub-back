package com.knusolution.datahub.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ReplyRepository: JpaRepository<ReplyEntity,Long> {
    fun findByQa(qaEntity: QAEntity): List<ReplyEntity>
    fun findByUser(user : UserEntity): List<ReplyEntity>
    fun findByReplyId(replyId:Long):ReplyEntity
}