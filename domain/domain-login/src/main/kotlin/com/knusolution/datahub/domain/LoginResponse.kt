package com.knusolution.datahub.domain

data class LoginResponse(
    val accessToken:String,
    val refreshToken: String,
    val user: UserDto,
)

fun UserDto.asLoginResponse(accessToken: String,refreshToken: String) =
    LoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = this
    )
