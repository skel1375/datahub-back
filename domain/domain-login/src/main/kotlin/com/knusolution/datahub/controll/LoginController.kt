package com.knusolution.datahub.controll

import com.knusolution.datahub.application.LoginService
import com.knusolution.datahub.domain.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import javax.servlet.http.HttpServletRequest

@RestController
class LoginController(
    private val loginService: LoginService
){
    //시스템 등록 (유저 등록)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/join")
    fun registerUser(@RequestBody req: JoinRequest) = loginService.registerUser(req)

    //로그인
    @PostMapping("/login/user")
    fun loginUser(@RequestBody req: LoginRequest) = loginService.loginUser(req)

    //로그인 Id 중복 검사
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/join/loginid")
    fun checkLoginId(@RequestParam loginId: String ) : ResponseEntity<Boolean> {
        return ResponseEntity.ok(loginService.checkLoginId(loginId))
    }
    //시스템 등록 시 시스템 이름 중복 검사
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/join/systemname")
    fun checkSystemName(@RequestParam systemName: String ) : ResponseEntity<Boolean> {
        return ResponseEntity.ok(loginService.checkSystemNameOnJoin(systemName))
    }
    //정보 수정 시 시스템 이름 중복 거사
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/user/update/systemname")
    fun checkSystemName(@RequestBody req: CheckSystemNameRequest) : ResponseEntity<Boolean> {
        return ResponseEntity.ok(loginService.checkSystemNameOnUpdate(req.loginId,req.systemName))
    }
    //유저시스템 정보 수정
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/user/update")
    fun updateUser(@RequestBody req:UpdateRequest) : ResponseEntity<Boolean> {
        return ResponseEntity.ok(loginService.updateUser(req))
    }

    //유저와 시스템정보 삭제
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/user/del")
    fun delUserSystem(@RequestParam systemId:Long)
    {
        loginService.delUserSystem(systemId)
    }
    
    //로그아웃
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/logout/user")
    fun logoutUser(request: HttpServletRequest, @RequestBody req: LogoutRequest){
        val authorizationHeader = request.getHeader("Authorization") ?: throw RuntimeException("No Authorization header")
        val token = authorizationHeader.removePrefix("Bearer ").trim()
        return loginService.logoutUser(token,req.loginId)
    }
    //유저 정보 전달
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/user/Info")
    fun getUserInfo(@RequestParam systemId: Long): InfoResponse
    {
        return loginService.getUserInfor(systemId)
    }
}