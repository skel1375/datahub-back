package com.knusolution.datahub.security.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UserRefreshTokenRepository: JpaRepository<UserRefreshTokenEntity, Long> {
    fun findByUserIdAndReissueCountLessThan(id: Long, count: Long): UserRefreshTokenEntity?
}