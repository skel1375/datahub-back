package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "Reply")
data class ReplyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val replyId: Long=0,

    @NotNull
    @Column
    val replyDate: String,

    @NotNull
    @Column(columnDefinition = "TEXT")
    var replyContent: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    val userId: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qaId")
    val qaId: QAEntity
)

fun ReplyDto.asEntity()=ReplyEntity(
    replyDate=this.replyDate,
    replyContent=this.replyContent,
    userId = this.userId,
    qaId = this.qaId
)
