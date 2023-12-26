package com.knusolution.datahub.domain

data class LoginResponse(
    val accessToken:String,
    val user: UserDto,
)

fun UserDto.asLoginResponse(accessToken: String) =
    LoginResponse(
        accessToken = accessToken,
        user = this
    )
