package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "notice")
data class NoticeEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val noticeId: Long = 0,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "userId")
        val user: UserEntity,

        @NotNull
        @Column
        var noticeTitle: String,

        @NotNull
        @Column
        var noticeDate: String,

        @NotNull
        @Column(columnDefinition = "TEXT")
        var noticeContent: String
)

fun NoticeDto.asEntity() = NoticeEntity(
        user = this.user,
        noticeTitle = this.noticeTitle,
        noticeDate = this.noticeDate,
        noticeContent = this.noticeContent
)

