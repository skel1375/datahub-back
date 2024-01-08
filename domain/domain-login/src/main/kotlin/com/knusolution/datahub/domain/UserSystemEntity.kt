package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.BaseCategoryEntity
import com.knusolution.datahub.system.domain.SystemEntity
import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name="user_system")
data class UserSystemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userSystemId:Long = 0,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "userId")
    val user:UserEntity,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "systemId")
    val system: SystemEntity
)

fun UserSystemDto.asEntity()=UserSystemEntity(
    user = this.user,
    system = this.system
)
