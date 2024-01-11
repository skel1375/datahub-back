package com.knusolution.datahub.security.domain

import com.knusolution.datahub.domain.UserEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne

@Entity
data class UserRefreshTokenEntity(
        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "userId")
        val user: UserEntity,
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
