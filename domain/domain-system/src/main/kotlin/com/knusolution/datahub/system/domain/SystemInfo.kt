package com.knusolution.datahub.system.domain

data class SystemInfo(
        val systemId: Long,
        val systemName: String,
        val isSystem: Boolean,
        val parentSystem : SystemEntity?
)
data class systemSummary(
        val systemId: Long,
        val systemName: String,
        val isSystem: Boolean,
        val parentSystem : SystemEntity?,
        val wait: Int,
        val decline: Int,
        val accept: Int,
        val averScore: Float?
)

fun SystemEntity.asSystemInfo() = SystemInfo(
        systemId = this.systemId,
        systemName = this.systemName,
        isSystem = this.isSystem,
        parentSystem = this.parentSystem
)

fun SystemEntity.asSystemSummary(wait:Int,decline:Int,accept:Int , averScore:Float?) = systemSummary(
        systemId = this.systemId,
        systemName = this.systemName,
        isSystem = this.isSystem,
        parentSystem = this.parentSystem,
        wait= wait,
        decline=decline,
        accept = accept,
        averScore = averScore
)