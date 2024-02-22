package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "notice")
data class NoticeEntity(
        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '공지사항 ID값'")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val noticeId: Long = 0,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "userId",columnDefinition = "BIGINT NOT NULL COMMENT '공지사항을 작성한 유저ID'")
        val user: UserEntity,

        @NotNull
        @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT '공지사항의 제목'")
        var noticeTitle: String,

        @NotNull
        @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT '공지사항 업로드일시(yyyy-MM-dd HH:mm 형태로저장)'")
        var noticeDate: String,

        @NotNull
        @Column(columnDefinition = "TEXT NOT NULL COMMENT '공지사항의 내용'",)
        var noticeContent: String
)

fun NoticeDto.asEntity() = NoticeEntity(
        user = this.user,
        noticeTitle = this.noticeTitle,
        noticeDate = this.noticeDate,
        noticeContent = this.noticeContent
)

