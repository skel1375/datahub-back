package com.knusolution.datahub.domain

import org.springframework.data.jpa.repository.JpaRepository

interface QARepository: JpaRepository<QAEntity,Long> {
    fun findByUserId(userId : UserEntity): List<QAEntity>
}