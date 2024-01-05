package com.knusolution.datahub.controll

import com.knusolution.datahub.domain.JoinRequest
import com.knusolution.datahub.domain.LoginRequest
import com.knusolution.datahub.application.LoginService
import com.knusolution.datahub.domain.UpdateRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class LoginController(
    private val loginService: LoginService
){
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/join/user")
    fun registerUser(@RequestBody req: JoinRequest):Boolean
    {
        if(!loginService.exitsUserByLoginId(req.loginId)) {
            loginService.registerUser(req)
            return true
        }
        return false
    }
    @PostMapping("/users")
    fun loginUser(@RequestBody req: LoginRequest) = loginService.loginUser(req)

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/users")
    fun updateUser(@RequestBody req:UpdateRequest) = loginService.updateUser(req)
}