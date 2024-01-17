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
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/join/user")
    fun registerUser(@RequestBody req: JoinRequest) = loginService.registerUser(req)

    @PostMapping("/users")
    fun loginUser(@RequestBody req: LoginRequest) = loginService.loginUser(req)

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/join/check-loginid")
    fun checkLoginId(@RequestParam loginId: String ) : ResponseEntity<Boolean> {
        return ResponseEntity.ok(loginService.checkLoginId(loginId))
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/join/check-systemname")
    fun checkSystemName(@RequestParam systemName: String ) : ResponseEntity<Boolean> {
        return ResponseEntity.ok(loginService.checkSystemNameOnJoin(systemName))
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/users/check-systemname")
    fun checkSystemName(@RequestBody req: CheckSystemNameRequest) : ResponseEntity<Boolean> {
        return ResponseEntity.ok(loginService.checkSystemNameOnUpdate(req.loginId,req.systemName))
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/users")
    fun updateUser(@RequestBody req:UpdateRequest) : ResponseEntity<Boolean> {
        return ResponseEntity.ok(loginService.updateUser(req))
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/user/del")
    fun delUserSystem(@RequestParam systemId:Long)
    {
        loginService.delUserSystem(systemId)
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/logout/user")
    fun logoutUser(request: HttpServletRequest, @RequestBody req: LogoutRequest){
        val authorizationHeader = request.getHeader("Authorization") ?: throw RuntimeException("No Authorization header")
        val token = authorizationHeader.removePrefix("Bearer ").trim()
        return loginService.logoutUser(token,req.loginId)
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/user/bysystem")
    fun getUserInfo(@RequestParam systemId: Long): InfoResponse
    {
        return loginService.getUserInfor(systemId)
    }
}