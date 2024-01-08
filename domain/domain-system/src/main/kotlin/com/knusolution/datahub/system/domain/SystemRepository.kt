package com.knusolution.datahub.system.domain

import org.springframework.data.jpa.repository.JpaRepository

interface SystemRepository:JpaRepository<SystemEntity,Long> {
<<<<<<< HEAD
    fun findBySystemId(systemId:Long):SystemEntity
=======
    fun existsBySystemName(systemName:String):Boolean
>>>>>>> origin/develop
}