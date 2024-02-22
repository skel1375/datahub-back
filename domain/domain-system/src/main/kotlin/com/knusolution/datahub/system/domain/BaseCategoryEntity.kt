package com.knusolution.datahub.system.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "base_category")
data class BaseCategoryEntity(
    @Id
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '베이스카테고리의 ID값'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val baseCategoryId:Long = 0,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT '산출물종류를 구분하는 카테고리'")
    val baseCategoryName:String,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "systemId",columnDefinition = "BIGINT NOT NULL COMMENT '베이스카테고리와 연결된 기관(system)ID값'")
    val system: SystemEntity
)

fun BaseCategoryDto.asEntity(system: SystemEntity) = BaseCategoryEntity(
    baseCategoryName = this.baseCategoryName,
    system = system)
