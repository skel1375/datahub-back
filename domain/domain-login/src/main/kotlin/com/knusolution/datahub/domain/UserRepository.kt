package com.knusolution.datahub.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository:JpaRepository<UserEntity,Long> {
    fun existsByLoginId(loginId:String):Boolean

    fun findByLoginId(loginId: String): UserEntity?

    fun findByUserId(userId : Long): UserEntity
    fun findByRole(role: Role):UserEntity
}