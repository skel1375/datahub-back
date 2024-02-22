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
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '유저와 시스템을 연결하는 유저시스템을 구분하는 ID값'")
    val userSystemId:Long = 0,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "userId",columnDefinition = "BIGINT NOT NULL COMMENT '시스템과 연결된 유저의 ID값'")
    val user:UserEntity,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "systemId",columnDefinition = "BIGINT NOT NULL COMMENT '유저와 연결된 시스템의 ID값'")
    val system: SystemEntity
)

fun UserSystemDto.asEntity()=UserSystemEntity(
    user = this.user,
    system = this.system
)
