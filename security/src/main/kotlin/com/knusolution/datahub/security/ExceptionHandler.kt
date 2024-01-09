package com.knusolution.datahub.security

import com.knusolution.datahub.security.domain.ApiResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class, NoSuchElementException::class)
    fun handleCommonException(e: Exception) = ResponseEntity.badRequest().body(e.message)

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException() = ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("접근이 거부되었습니다."))

    @ExceptionHandler(InsufficientAuthenticationException::class)
    fun handleInsufficientAuthenticationException() = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("인증에 실패하였습니다."))

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException() = ResponseEntity.internalServerError().body(ApiResponse.error("서버에 문제가 발생했습니다."))

    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException() = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("토큰이 유효하지 않습니다."))

    @ExceptionHandler(MalformedJwtException::class)
    fun handleMalformedJwtException() = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("올바르지 않은 토큰입니다."))

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException() = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("토큰이 만료되었습니다. 다시 로그인해주세요."))
}