package com.knusolution.datahub.system.domain

import org.springframework.data.jpa.repository.JpaRepository

interface SystemRepository:JpaRepository<SystemEntity,Long> {
    fun findBySystemId(systemId:Long):SystemEntity
    fun existsBySystemName(systemName:String):Boolean
    fun existsByParentSystem(system:SystemEntity): Boolean
    fun findByParentSystem(systemEntity: SystemEntity):List<SystemEntity>
    fun findByParentSystemAndIsSystem(systemEntity: SystemEntity , isSystem: Boolean):List<SystemEntity>?
}