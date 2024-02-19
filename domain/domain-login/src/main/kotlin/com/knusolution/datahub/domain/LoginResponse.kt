package com.knusolution.datahub.domain

data class LoginResponse(
    val accessToken:String,
    val refreshToken: String,
    val user: UserDto,
    val systemId : Long
)

fun UserDto.asLoginResponse(accessToken: String,refreshToken: String, systemId: Long) =
    LoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = this,
        systemId = systemId
    )
