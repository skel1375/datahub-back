package com.knusolution.datahub.security.domain

import org.jetbrains.annotations.NotNull
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "blacklist")
data class BlackListEntity(
        @Id
        @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT '블랙리스트에 들어간 토큰값'")
        val token: String,

        @NotNull
        @Column(columnDefinition = "DATE NOT NULL COMMENT '토큰의 만료기한'")
        val expireDate: Date
)
