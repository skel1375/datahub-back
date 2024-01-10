package com.knusolution.datahub.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.knusolution.datahub.security.domain.UserRefreshTokenRepository
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class TokenProvider (
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
    @Value("\${jwt.access-expiration-minutes}")
    private val accessExpirationMinutes: Long,
    @Value("\${jwt.refresh-expiration-hours}")
    private val refreshExpirationHours: Long,
    @Value("\${jwt.issuer}")
    private val issuer: String,
    private val userRefreshTokenRepository: UserRefreshTokenRepository
) {
    private val reissueLimit = refreshExpirationHours * 60 / accessExpirationMinutes
    private  val objectMapper = ObjectMapper() // JWT 역직렬화를 위함
    fun createToken(userSpecification: String) = Jwts.builder()
        .signWith(SecretKeySpec(secretKey.toByteArray(), SignatureAlgorithm.HS512.jcaName)) // HS512 알고리즘을 사용하여 secretKey를 이용해 서명
        .setSubject(userSpecification)   // JWT 토큰 제목
        .setIssuer(issuer)    // JWT 토큰 발급자
        .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))    // JWT 토큰 발급 시간
        .setExpiration(Date.from(Instant.now().plus(accessExpirationMinutes, ChronoUnit.SECONDS)))    // JWT 토큰의 만료시간 설정
        .compact()!!    // JWT 토큰 생성
    fun createRefreshToken() = Jwts.builder()
        .signWith(SecretKeySpec(secretKey.toByteArray(),SignatureAlgorithm.HS512.jcaName))
        .setIssuer(issuer)
        .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
        .setExpiration(Date.from((Instant.now().plus(refreshExpirationHours,ChronoUnit.HOURS))))
        .compact()!!

    @Transactional
    fun recreateAccessToken(oldAccessToken: String): String {
        val subject = decodeJwtPayloadSubject(oldAccessToken)
        userRefreshTokenRepository.findByUserIdAndReissueCountLessThan(subject.split(':')[0].toLong(),reissueLimit)
            ?.increaseReissueCount() ?: throw ExpiredJwtException(null, null, "Refresh token이 만료되었습니다.")
        return createToken(subject)
    }
    @Transactional(readOnly = true)
    fun validateRefreshToken(refreshToken: String, oldAccessToken: String) {
        println("check2 $refreshToken")
        validateToken(refreshToken)
        val userID = decodeJwtPayloadSubject(oldAccessToken).split(':')[0].toLong()
        userRefreshTokenRepository.findByUserIdAndReissueCountLessThan(userID,reissueLimit)
            ?.takeIf { it.validateRefreshToken(refreshToken) } ?: throw ExpiredJwtException(null,null,"Refresh token이 만료되었습니다.")
    }

    //Token : header.payload.signature이므로 [1]은 payload
    private fun decodeJwtPayloadSubject(oldAccessToken: String) =
        objectMapper.readValue(
            Base64.getUrlDecoder().decode(oldAccessToken.split('.')[1]).decodeToString(),
            Map::class.java
        )["sub"].toString()

    fun validateToken(token: String) =
        Jwts.parserBuilder().setSigningKey(secretKey.toByteArray()).build().parseClaimsJws(token)!!
    fun getSubject(token: String) = validateToken(token).body.subject!!
    fun getExpireDate(token: String): Date = validateToken(token).body.expiration
}