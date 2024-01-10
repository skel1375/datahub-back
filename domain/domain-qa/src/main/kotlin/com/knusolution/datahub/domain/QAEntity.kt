package com.knusolution.datahub.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "QA")
data class QAEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val qaId: Long=0,

    @NotNull
    @Column
    var qaTitle: String,

    @NotNull
    @Column
    var qaDate: String,

    @NotNull
    @Column(columnDefinition = "TEXT")
    var qaContent: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    val user: UserEntity
)

fun QADto.asEntity() = QAEntity(
    qaTitle = this.qaTitle,
    qaDate =  this.qaDate,
    qaContent = this.qaContent,
    user = this.user
)
