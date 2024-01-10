package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.SystemEntity

data class UserSystemDto(
    val user:UserEntity,
    val system:SystemEntity
)

fun UserSystemEntity.asDto() = UserSystemDto(
    user = this.user,
    system = this.system
)