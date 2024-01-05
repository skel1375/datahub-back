package com.knusolution.datahub.controll

import com.knusolution.datahub.domain.JoinRequest
import com.knusolution.datahub.domain.LoginRequest
import com.knusolution.datahub.application.LoginService
import com.knusolution.datahub.domain.UpdateRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class LoginController(
    private val loginService: LoginService
){
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/join/user")
    fun registerUser(@RequestBody req: JoinRequest) = loginService.registerUser(req)
    @PreAuthorize("permitAll()")
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
        return ResponseEntity.ok(loginService.checkSystemName(systemName))
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/users")
    fun updateUser(@RequestBody req:UpdateRequest) = loginService.updateUser(req)
}