package com.knusolution.datahub.system.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*
import javax.validation.constraints.Null

@Entity
@Table(name = "DetailCategory")
data class DetailCategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val detailCategoryId:Long = 0,

    @NotNull
    @Column
    var detailCategoryName:String,

    @Column
    var finalApproval:String?,

    @Column
    var finalScore:Int?,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "baseCategoryId")
    val baseCategory: BaseCategoryEntity
)

fun DetailCategoryDto.asEntity(baseCategory: BaseCategoryEntity) = DetailCategoryEntity(
    detailCategoryName = this.detailCategoryName,
    finalApproval = this.finalApproval,
    finalScore = this.finalScore,
    baseCategory = baseCategory
)