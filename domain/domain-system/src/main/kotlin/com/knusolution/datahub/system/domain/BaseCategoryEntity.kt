package com.knusolution.datahub.system.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "BaseCategory")
data class BaseCategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val baseCategoryId:Long = 0,

    @NotNull
    @Column
    val baseCategoryName:String,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "systemId")
    val system: SystemEntity
)

fun BaseCategoryDto.asEntity(system: SystemEntity) = BaseCategoryEntity(
    baseCategoryName = this.baseCategoryName,
    system = system)
