package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.SystemEntity
import org.jetbrains.annotations.NotNull
import org.springframework.security.crypto.password.PasswordEncoder
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "user")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId:Long = 0,

    @NotNull
    @Column
    var loginId:String,

    @NotNull
    @Column
    var password:String,

    @NotNull
    @Column
    var companyName:String,

    @NotNull
    @Column
    var developerName:String,

    @NotNull
    @Column
    var contactNum:String,

    @NotNull
    @Column
    var department:String,

    @NotNull
    @Column
    var departmentName:String,

    @NotNull
    @Column
    val role: Role,

    @ManyToMany
    @JoinTable(name = "user_system",
        joinColumns = [JoinColumn(name = "userId")],
        inverseJoinColumns = [JoinColumn(name = "systemId")]
    )
    var systems: MutableSet<SystemEntity> = HashSet()
)
enum class Role{
    ADMIN,USER
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
