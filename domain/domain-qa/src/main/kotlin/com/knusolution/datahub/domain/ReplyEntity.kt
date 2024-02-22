package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "reply")
data class ReplyEntity(
    @Id
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT 'Q&A 답글의 ID값'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val replyId: Long=0,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT 'Q&A 답글의 업로드 시간(yyyy-MM-dd HH:mm 형태로저장)'")
    var replyDate: String,

    @NotNull
    @Column(columnDefinition = "TEXT NOT NULL COMMENT 'Q&A 답글의 내용'")
    var replyContent: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",columnDefinition = "BIGINT NOT NULL COMMENT 'QA와 연결된 유저ID'")
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qaId",columnDefinition = "BIGINT NOT NULL COMMENT 'Q&A의 답글과 연결된 Q&A ID값'")
    val qa: QAEntity
)

fun ReplyDto.asEntity()=ReplyEntity(
    replyDate=this.replyDate,
    replyContent=this.replyContent,
    user = this.user,
    qa = this.qa
)
