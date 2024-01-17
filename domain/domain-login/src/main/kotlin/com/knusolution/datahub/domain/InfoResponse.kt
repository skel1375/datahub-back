package com.knusolution.datahub.domain

data class InfoResponse(
    val systemName:String,
    val user: UserDto
)

fun UserDto.asInfoResponse(systemName: String)=
    InfoResponse(
        systemName = systemName,
        user = this
    )
