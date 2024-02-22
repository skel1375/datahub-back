package com.knusolution.datahub.system.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*
import javax.validation.constraints.Null

@Entity
@Table(name = "DetailCategory")
data class DetailCategoryEntity(
    @Id
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '디테일카테고리(산출물)ID값'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val detailCategoryId:Long = 0,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT '디테일카테고리(산출물)명'")
    var detailCategoryName:String,

    @Column(columnDefinition = "VARCHAR(20) NULL COMMENT '최종 승인여부(대기,반려,승인)'")
    var finalApproval:String?,

    @Column(columnDefinition = "INT NULL COMMENT '최종 점수(점수가 없는 경우 null)'")
    var finalScore:Int?,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "baseCategoryId",columnDefinition = "BIGINT NOT NULL COMMENT '디테일카테고리(산출물)과 연결되는 베이스카테고리ID값'")
    val baseCategory: BaseCategoryEntity
)

fun DetailCategoryDto.asEntity(baseCategory: BaseCategoryEntity) = DetailCategoryEntity(
    detailCategoryName = this.detailCategoryName,
    finalApproval = this.finalApproval,
    finalScore = this.finalScore,
    baseCategory = baseCategory
)