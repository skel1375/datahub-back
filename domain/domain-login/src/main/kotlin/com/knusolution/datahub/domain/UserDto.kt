package com.knusolution.datahub.domain

data class UserDto(
    val loginId:String,
    val companyName:String,
    val developerName:String,
    val contactNum:String,
    val department:String,
    val departmentName:String,
    val role: Role,
    val systemIds : List<Long> = listOf(),
)

fun UserEntity.asUserDto(systemIds:List<Long> = listOf()) = UserDto(
    loginId = loginId,
    companyName = companyName,
    developerName = developerName,
    contactNum = contactNum,
    department = department,
    departmentName = departmentName,
    role = role,
    systemIds = systemIds,
)