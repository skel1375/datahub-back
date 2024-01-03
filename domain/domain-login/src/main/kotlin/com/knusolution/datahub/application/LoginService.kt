package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import com.knusolution.datahub.domain.UserRepository
import com.knusolution.datahub.security.TokenProvider
import com.knusolution.datahub.system.domain.*
import org.springframework.security.crypto.password.PasswordEncoder
import javax.transaction.Transactional

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val baseCategoryRepository: BaseCategoryRepository,
    private val detailCategoryRepository: DetailCategoryRepository,
    private val encoder: PasswordEncoder,
    private val tokenProvider: TokenProvider
){
    fun registerUser(req: JoinRequest){
        val system = systemRepository.save(req.asSystemDto().asEntity())
        val user = req.asUserDto().asEntity(password = req.loginId, encoder)
        user.systems.add(system)
        userRepository.save(user)
        registerCategory(system)
    }
    private fun registerCategory(system: SystemEntity)
    {
        getBaseCategory().forEach {
            val baseCategoryEntity = it.asEntity(system)
            baseCategoryRepository.save(baseCategoryEntity)
            getDetailCategory(it.baseCategoryName).forEach {
                    detail-> detailCategoryRepository.save(detail.asEntity(baseCategoryEntity))
            }
        }
    }
    fun loginUser(req: LoginRequest): LoginResponse?
    {
        val userEntity = userRepository.findByLoginId(req.loginId)
        val userDto = userEntity?.let { it ->
            val systemIds = it.systems.toList().map { system -> system.systemId }
            if(encoder.matches(req.password,it.password)) it.asUserDto(systemIds = systemIds) else null
        }
        val token = tokenProvider.createToken("${userDto?.loginId}:${userDto?.role}")
        return userDto?.asLoginResponse(token)
    }
    fun exitsUserByLoginId(loginId:String) = userRepository.existsByLoginId(loginId)
    fun updateUser(req:UpdateRequest)
    {
        val userEntity = userRepository.findByLoginId(req.loginId)
        userEntity?.let {
            req.updateUserEntity(it,encoder)
            updateDBSystem(it,req)
            userRepository.save(it)
        }
    }
    fun updateDBSystem(userEntity: UserEntity,req:UpdateRequest)
    {
        val system = userEntity.systems?.firstOrNull()
        system?.let {
            it.systemName = req.systemName
            systemRepository.save(system)
        }
    }
}