package com.knusolution.datahub.system.domain

data class SystemResponse(
    val systems : List<SystemInfo>,
)

data class SystemPageResponse(
    val organizations : List<SystemInfo>?,
    val systems:List<SystemInfo>?
)
