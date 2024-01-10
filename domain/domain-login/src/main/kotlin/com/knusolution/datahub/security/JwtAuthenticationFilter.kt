package com.knusolution.datahub.security

import com.knusolution.datahub.security.domain.BlackListRepository
import io.jsonwebtoken.JwtException
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
            val token = parseBearerToken(request)
            tokenProvider.validateToken(token!!)

            if(blackListRepository.existsByToken(token)) throw JwtException("Token-Invalid, 로그아웃으로 만료된 토큰입니다.")

            val user = parseUserSpecification(token)
            UsernamePasswordAuthenticationToken.authenticated(user, token, user.authorities)
                    .apply { details = WebAuthenticationDetails(request) }
                    .also { SecurityContextHolder.getContext().authentication = it }
        } catch (e: Exception) {
            request.setAttribute("exception",e)
        }

        filterChain.doFilter(request, response)
    }

    private fun parseBearerToken(request: HttpServletRequest) = request.getHeader(HttpHeaders.AUTHORIZATION)
            .takeIf { it?.startsWith("Bearer ", true) ?: false }?.substring(7)

    private fun parseUserSpecification(token: String?) = (
            token?.takeIf { it.length >= 10 }
                    ?.let { tokenProvider.getSubject(it) }
                    ?: "anonymous:anonymous"
            ).split(":")
            .let { User(it[0], "", listOf(SimpleGrantedAuthority(it[1]))) }
}