package com.knusolution.datahub.system.domain

data class SystemInfo(
        val systemId: Long,
        val systemName: String
)

fun SystemEntity.asSystemInfo() = SystemInfo(
        systemId = this.systemId,
        systemName = this.systemName
)