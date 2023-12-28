package com.knusolution.datahub.controll

import com.knusolution.datahub.domain.JoinRequest
import com.knusolution.datahub.domain.LoginRequest
import com.knusolution.datahub.application.LoginService
import com.knusolution.datahub.domain.UpdateRequest
import org.springframework.web.bind.annotation.*

@RestController
class LoginController(
    private val loginService: LoginService
){
    @PostMapping("/join/user")
    fun registerUser(@RequestBody req: JoinRequest){
        if(!loginService.exitsUserByLoginId(req.loginId))
            loginService.registerUser(req)
    }
    @PostMapping("/users")
    fun loginUser(@RequestBody req: LoginRequest) = loginService.loginUser(req)
    @PutMapping("/users")
    fun updateUser(@RequestBody req:UpdateRequest) = loginService.updateUser(req)
}