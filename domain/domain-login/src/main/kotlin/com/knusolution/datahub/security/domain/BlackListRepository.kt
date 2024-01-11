package com.knusolution.datahub.security.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BlackListRepository: JpaRepository<BlackListEntity, Long> {
    fun existsByToken(token: String): Boolean
    fun deleteAllByExpireDateBefore(expireDate: Date)
}