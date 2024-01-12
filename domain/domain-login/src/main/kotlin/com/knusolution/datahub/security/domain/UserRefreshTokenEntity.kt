package com.knusolution.datahub.security.domain

import com.knusolution.datahub.domain.UserEntity
import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "user_refreshtoken")
data class UserRefreshTokenEntity(
        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "userId")
        val user: UserEntity,
        @NotNull
        private var refreshToken: String
){
        @Id
        val userId: Long? = null
        private var reissueCount: Long = 0

        fun updateRefreshToken(refreshToken: String){
                this.refreshToken = refreshToken
                this.reissueCount = 0
        }

        fun validateRefreshToken(refreshToken: String) = this.refreshToken == refreshToken

        fun increaseReissueCount(){
                reissueCount++
        }
}
