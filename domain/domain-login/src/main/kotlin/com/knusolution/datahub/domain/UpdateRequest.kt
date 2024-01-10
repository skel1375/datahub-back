package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.SystemDto
import org.springframework.security.crypto.password.PasswordEncoder

data class UpdateRequest(
    val loginId:String, // 로그인 아이디
    val password:String,// 로그인 비밀번호

    val systemName:String,// 시스템명

    val departmentName:String,// 시스템 담당 공무원 이름
    val department:String,// 시스템 담당 공무원 부서

    val companyName:String,//시스템 유지보수 업체 이름
    val developerName:String,//시스템 유지보수 업체 개발자 이름
    val contactNum:String,// 시스템 유지보수 업체 연락처
)

fun UpdateRequest.updateUserEntity(userEntity: UserEntity,encoder: PasswordEncoder) = userEntity.also {


    it.loginId = this.loginId
    it.password = encoder.encode(this.password)
    it.departmentName = this.departmentName
    it.department = this.department
    it.contactNum = this.contactNum
}
fun UpdateRequest.asUserDto() = UserDto(
    loginId = this.loginId,
    companyName = this.companyName,
    department = this.department,
    departmentName = this.departmentName,
    developerName = this.developerName,
    contactNum = this.contactNum,
    role = Role.USER,
)

fun UpdateRequest.asSystemDto() = SystemDto(
    systemName = this.systemName
)