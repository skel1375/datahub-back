package com.knusolution.datahub.security.domain

import org.jetbrains.annotations.NotNull
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class BlackListEntity(
        @Id
        val token: String,

        @NotNull
        @Column
        val expireDate: Date
)
