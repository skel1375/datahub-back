package com.knusolution.datahub.domain

import com.knusolution.datahub.system.domain.DetailCategoryEntity
import org.jetbrains.annotations.NotNull
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.*

@Entity
@Table(name = "article")
data class ArticleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val articleId: Long=0,

    @NotNull
    @Column
    val uploadDate : String,

    @NotNull
    @Column
    var approval : String,

    @Column
    var declineDetail : String,

    @NotNull
    @Column
    val taskFileUrl : String,

    @NotNull
    @Column
    val taskFileName : String,

    @Column
    var declineFileUrl : String,

    @Column
    var declineFileName : String,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "detailCategoryId")
    val detailCategory: DetailCategoryEntity
    )

fun ArticleDto.asEntity( ) = ArticleEntity(
    uploadDate = this.uploadDate,
    approval = this.approval,
    declineDetail = this.declineDetail,
    taskFileUrl = this.taskFileUrl,
    taskFileName = this.taskFileName,
    declineFileUrl = this.declineFileUrl,
    declineFileName = this.declineFileName,
    detailCategory = this.detailCategory
)