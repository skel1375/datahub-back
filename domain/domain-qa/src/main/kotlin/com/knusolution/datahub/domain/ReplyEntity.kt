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
    var replyDate: String,

    @NotNull
    @Column(columnDefinition = "TEXT")
    var replyContent: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qaId")
    val qa: QAEntity
)

fun ReplyDto.asEntity()=ReplyEntity(
    replyDate=this.replyDate,
    replyContent=this.replyContent,
    user = this.user,
    qa = this.qa
)
