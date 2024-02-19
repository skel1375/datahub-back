package com.knusolution.datahub.system.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*
@Entity
@Table(name = "DetailCategory")
data class DetailCategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val detailCategoryId:Long = 0,

    @NotNull
    @Column
    var detailCategoryName:String,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "baseCategoryId")
    val baseCategory: BaseCategoryEntity
)

fun DetailCategoryDto.asEntity(baseCategory: BaseCategoryEntity) = DetailCategoryEntity(
    detailCategoryName = this.detailCategoryName,
    baseCategory = baseCategory
)