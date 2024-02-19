package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.SystemEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserSystemRepository:JpaRepository<UserSystemEntity,Long> {
    fun findByUser(user: UserEntity): UserSystemEntity
    fun findBySystem(system: SystemEntity): UserSystemEntity
    fun findByUserAndSystem(userEntity: UserEntity, systemEntity: SystemEntity): UserSystemEntity
    fun findBySystemSystemId(systemId: Long): UserSystemEntity
}