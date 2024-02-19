package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.SystemDto

data class JoinRequest(
    val systemName:String,// 시스템명

    val departmentName:String,// 시스템 담당 공무원 이름
    val department:String,// 시스템 담당 공무원 부서

    val companyName:String,//시스템 유지보수 업체 이름
    val developerName:String,//시스템 유지보수 업체 개발자 이름
    val contactNum:String,// 시스템 유지보수 업체 연락처

    val loginId:String,// 임시로 사용할 로그인 아이디/비밀번호

    val role: Role,
    val parentSystemId:Long
)

fun JoinRequest.asUserDto() = UserDto(
    loginId = this.loginId,
    companyName = this.companyName,
    department = this.department,
    departmentName = this.departmentName,
    developerName = this.developerName,
    contactNum = this.contactNum,
    role = this.role,
)

fun JoinRequest.asSystemDto() = SystemDto(
    systemName = this.systemName,
    isSystem = if (role == Role.USER) true else false
)
