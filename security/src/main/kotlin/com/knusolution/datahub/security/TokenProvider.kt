package com.knusolution.datahub.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
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
    @Value("\${jwt.expiration-hours}")
    private val expirationHours: Long,
    @Value("\${jwt.issuer}")
    private val issuer: String
) {
    fun createToken(userSpecification: String) = Jwts.builder()
            .signWith(SecretKeySpec(secretKey.toByteArray(), SignatureAlgorithm.HS512.jcaName)) // HS512 알고리즘을 사용하여 secretKey를 이용해 서명
            .setSubject(userSpecification)   // JWT 토큰 제목
            .setIssuer(issuer)    // JWT 토큰 발급자
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))    // JWT 토큰 발급 시간
            .setExpiration(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)))    // JWT 토큰의 만료시간 설정
            .compact()!!    // JWT 토큰 생성
    fun validateToken(token: String) : Boolean {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.toByteArray())
            return true
        } catch (e: JwtException){
            throw JwtException("TOKEN_INVALID")
        }
    }
    fun getSubject(token: String): String? {
        return getClaims(token).subject
    }
    fun getClaims(token: String): Claims {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey.toByteArray())
                    .build().parseClaimsJws(token)
                    .body
        } catch (e: JwtException){
            throw JwtException("TOKEN_INVALID")
        }
    }
    fun getExpireDate(token: String) = getClaims(token).expiration
}

