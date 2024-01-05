package com.knusolution.datahub

import com.knusolution.datahub.security.JwtAuthenticationFilter
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@EnableMethodSecurity
@Configuration
class SecurityConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {
    @Bean
    fun filterChain(http: HttpSecurity) = http
            .csrf().disable()
            .headers { it.frameOptions().sameOrigin() }	// H2 콘솔 사용을 위한 설정
            .authorizeHttpRequests {
                it.requestMatchers(
                        AntPathRequestMatcher("/"),
                        AntPathRequestMatcher("/swagger-ui/**"),
                        AntPathRequestMatcher("/v3/**"),
                        AntPathRequestMatcher("/users")
                ).permitAll()	// requestMatchers의 인자로 전달된 url은 모두에게 허용
                        .requestMatchers(PathRequest.toH2Console()).permitAll()	// H2 콘솔 접속은 모두에게 허용
                        .anyRequest().authenticated()	// 그 외의 모든 요청은 인증 필요
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }	// 세션을 사용하지 않으므로 STATELESS 설정
            .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter::class.java)
            .build()!!
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}