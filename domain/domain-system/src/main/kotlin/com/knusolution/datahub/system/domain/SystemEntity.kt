package com.knusolution.datahub.system.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "db_system")
data class SystemEntity(
    @Id
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '기관(시스템)고유아이디를 알 수 없어서 bigint로 설정'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val systemId:Long = 0,

    @NotNull
    @Column(unique = true,columnDefinition = "VARCHAR(20) NOT NULL COMMENT '기관(시스템)이름'")
    var systemName:String,

    @NotNull
    @Column(columnDefinition = "BOOLEAN NOT NULL COMMENT 'true면 system,false면 기관'")
    val isSystem :Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentSystemId" , columnDefinition = "BIGINT NULL DEFAULT NULL COMMENT '부모기관의 ID값'")
    var parentSystem: SystemEntity? = null,
)

fun SystemDto.asEntity() = SystemEntity(
    systemName = this.systemName,
    isSystem = this.isSystem
)
