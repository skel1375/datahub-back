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
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '게시물의 ID값'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val articleId: Long=0,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT 'yyyy-MM-dd HH:mm 형태로 저장'")
    val uploadDate : String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(10) NOT NULL DEFAULT '대기' COMMENT '업로드된 파일에 대한 승인여부(대기,반려,승인)'")
    var approval : String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(500) NOT NULL COMMENT '검토대상 파일의 파일URL'")
    val taskFileUrl : String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT '검토대상 파일의 원본 파일명'")
    val taskFileName : String,

    @Column(columnDefinition = "VARCHAR(255) NULL COMMENT '검토 반려에 대한 상세내역'")
    var declineDetail : String,

    @Column(columnDefinition = "VARCHAR(500) NULL COMMENT '검토 반려 상세 파일의 파일URL'")
    var declineFileUrl : String,

    @Column(columnDefinition = "VARCHAR(255) NULL COMMENT '검토 반려 상세 파일의 원본 파일명'")
    var declineFileName : String,

    @Column(columnDefinition = "INT NULL COMMENT '작업파일 현행화 평가점수(점수가 없을때 NULL)'")
    var score: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "detailCategoryId",columnDefinition = "BIGINT NOT NULL COMMENT '연결된 세부카테고리(산출물 종류)ID'")
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
    score = this.score,
    detailCategory = this.detailCategory
)