package com.knusolution.datahub.system.domain

data class SystemInfo(
        val systemId: Long,
        val systemName: String,
        val isSystem: Boolean,
        val parentSystem : SystemEntity?
)

fun SystemEntity.asSystemInfo() = SystemInfo(
        systemId = this.systemId,
        systemName = this.systemName,
        isSystem = this.isSystem,
        parentSystem = this.parentSystem
)