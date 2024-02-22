package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "QA")
data class QAEntity(
    @Id
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT 'Q&A의 ID값'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val qaId: Long=0,

    @NotNull
    @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT 'Q&A의 제목'")
    var qaTitle: String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(20) NOT NULL COMMENT 'Q&A 업로드 시간(yyyy-MM-dd HH:mm 형태로 저장)'")
    var qaDate: String,

    @NotNull
    @Column(columnDefinition = "TEXT NOT NULL COMMENT 'Q&A의 내용'")
    var qaContent: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", columnDefinition = "BIGINT NOT NULL COMMENT 'Q&A의 작성자(유저)ID값'")
    val user: UserEntity
)

fun QADto.asEntity() = QAEntity(
    qaTitle = this.qaTitle,
    qaDate =  this.qaDate,
    qaContent = this.qaContent,
    user = this.user
)
