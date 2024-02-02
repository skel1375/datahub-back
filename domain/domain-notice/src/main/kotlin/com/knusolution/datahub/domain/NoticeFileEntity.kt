package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "notice_file")
data class NoticeFileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val fileId: Long=0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticeId")
    val notice: NoticeEntity,

    @NotNull
    @Column(length = 500)
    val fileUrl: String,

    @NotNull
    @Column
    val fileName: String
)
fun NoticeFileDto.asEntity() = NoticeFileEntity(
    notice = this.notice,
    fileUrl = this.fileUrl,
    fileName = this.fileName,
)
