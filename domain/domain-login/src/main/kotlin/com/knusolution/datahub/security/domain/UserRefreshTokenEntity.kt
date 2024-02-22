package com.knusolution.datahub.security.domain

import com.knusolution.datahub.domain.UserEntity
import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "user_refreshtoken")
data class UserRefreshTokenEntity(
        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "userId",columnDefinition = "BIGINT NOT NULL COMMENT 'refresh토큰과 연결된 유저ID'")
        val user: UserEntity,

        @NotNull
        @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT 'accessToken재발급을 위한 refreshToken을 저장'")
        private var refreshToken: String
){
        @Id
        val userId: Long? = null
        @Column(columnDefinition = "INT NOT NULL COMMENT 'accessToken이 재발급된 횟수'")
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
