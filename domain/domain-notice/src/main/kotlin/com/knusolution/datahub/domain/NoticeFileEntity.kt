package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "notice_file")
data class NoticeFileEntity(
    @Id
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '공지사항과 연결된 파일 ID'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val fileId: Long=0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticeId",columnDefinition = "BIGINT NOT NULL COMMENT '연결된 공지사항ID'")
    val notice: NoticeEntity,

    @NotNull
    @Column(columnDefinition = "VARCHAR(500) NOT NULL COMMENT '공지사항 첨부파일이 저장된 파일URL'")
    val fileUrl: String,

    @NotNull
    @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT '공지사항 첨부파일의 원본파일명'")
    val fileName: String
)
fun NoticeFileDto.asEntity() = NoticeFileEntity(
    notice = this.notice,
    fileUrl = this.fileUrl,
    fileName = this.fileName,
)
