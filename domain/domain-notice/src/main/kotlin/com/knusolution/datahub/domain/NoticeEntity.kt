package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
data class NoticeEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val noticeId: Long = 0,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        val userId: UserEntity,

        @NotNull
        @Column
        val noticeTitle: String,

        @NotNull
        @Column
        val noticeDate: String,

        @NotNull
        @Column
        val noticeContent: String
)

fun NoticeDto.asEntity() = NoticeEntity(
        noticeId = this.noticeId,
        userId = this.userId,
        noticeTitle = this.noticeTitle,
        noticeDate = this.noticeDate,
        noticeContent = this.noticeContent
)

