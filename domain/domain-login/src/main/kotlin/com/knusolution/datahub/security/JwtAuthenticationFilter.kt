package com.knusolution.datahub.security

import com.knusolution.datahub.security.domain.BlackListRepository
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Order(1)
@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val blackListRepository: BlackListRepository)
    : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try{
            val accessToken = parseBearerToken(request,HttpHeaders.AUTHORIZATION) // 토큰 파싱
            //블랙리스트에서 로그아웃된 토큰인지 검사
            if(blackListRepository.existsByToken(accessToken!!)) throw ExpiredJwtException(null,null,"로그아웃으로 만료된 토큰")
            val user = parseUserSpecification(accessToken)
            UsernamePasswordAuthenticationToken.authenticated(user, accessToken, user.authorities)
                    .apply { details = WebAuthenticationDetails(request) }
                    .also { SecurityContextHolder.getContext().authentication = it }
        } catch (e: ExpiredJwtException) {
            reissueAccessToken(request, response, e)
        } catch (e: Exception) {
            request.setAttribute("exception",e)
        }
        filterChain.doFilter(request, response)
    }

    private fun parseBearerToken(request: HttpServletRequest, headerName: String)
    = request.getHeader(headerName)
            .takeIf { it?.startsWith("Bearer ", true) ?: false }?.substring(7)

    //토큰에서 유저 정보 파싱
    private fun parseUserSpecification(token: String?) = (
            token?.takeIf { it.length >= 10 }
                    ?.let { tokenProvider.getSubject(it) }
                    ?: "anonymous:anonymous"
            ).split(":")
            .let { User(it[0], "", listOf(SimpleGrantedAuthority(it[1]))) }

    private fun reissueAccessToken(request: HttpServletRequest, response: HttpServletResponse, exception:Exception) {
        try {
            val refreshToken = parseBearerToken(request, "Refresh-Token") ?: throw exception
            val oldAccessToken = parseBearerToken(request, HttpHeaders.AUTHORIZATION)!!
            tokenProvider.validateRefreshToken(refreshToken, oldAccessToken)
            val newAccessToken = tokenProvider.recreateAccessToken(oldAccessToken)
            val user = parseUserSpecification(newAccessToken)
            UsernamePasswordAuthenticationToken.authenticated(user, newAccessToken, user.authorities)
                .apply { details = WebAuthenticationDetails(request) }
                .also { SecurityContextHolder.getContext().authentication = it }
            response.setHeader("new-access-token", newAccessToken)
        } catch (e: Exception) {
            request.setAttribute("exception", e)
        }
    }
}