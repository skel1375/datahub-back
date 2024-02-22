package com.knusolution.datahub.domain
import org.jetbrains.annotations.NotNull
import org.springframework.security.crypto.password.PasswordEncoder
import javax.persistence.*

@Entity
@Table(name = "user")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '유저를 구분하는 ID값'")
    val userId:Long = 0,

    @NotNull
    @Column(unique = true ,columnDefinition = "VARCHAR(20) NOT NULL COMMENT '유저 로그인시 사용하는 로그인아이디'")
    var loginId:String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT '유저 로그인시 사용하는 비밀번호'")
    var password:String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT '유지보수업체명'")
    var companyName:String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT '유지보수업체 담당자명'")
    var developerName:String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT '기관(시스템)담당자연락처'")
    var contactNum:String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT '기관(시스템)딤딩부서명'")
    var department:String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT '기관(시스템)담당자명'")
    var departmentName:String,

    @NotNull
    @Column(columnDefinition = "INT NOT NULL COMMENT '0:ADMIN(상위기관(도청)) 1:MIDDLE(하위기관(구청)) 2:USER(시스템)'")
    val role: Role,
)
enum class Role{
    ADMIN,MIDDLE,USER
}

fun UserDto.asEntity(password: String, encoder: PasswordEncoder) =
    UserEntity(
        loginId = this.loginId,
        password = encoder.encode(password),
        companyName = this.companyName,
        developerName = this.developerName,
        contactNum = this.contactNum,
        department = this.department,
        departmentName = this.departmentName,
        role = this.role
    )
