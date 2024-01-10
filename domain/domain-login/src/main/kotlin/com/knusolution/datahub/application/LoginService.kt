package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import com.knusolution.datahub.domain.UserRepository
import com.knusolution.datahub.security.domain.BlackListEntity
import com.knusolution.datahub.security.domain.BlackListRepository
import com.knusolution.datahub.security.TokenProvider
import com.knusolution.datahub.security.domain.UserRefreshTokenEntity
import com.knusolution.datahub.security.domain.UserRefreshTokenRepository
import com.knusolution.datahub.system.domain.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val blackListRepository: BlackListRepository,
    private val baseCategoryRepository: BaseCategoryRepository,
    private val detailCategoryRepository: DetailCategoryRepository,
    private val encoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val userRefreshTokenRepository: UserRefreshTokenRepository
){
    fun registerUser(req: JoinRequest): Boolean {
        if(checkDuplicate(req.loginId,req.systemName)){
            val system = systemRepository.save(req.asSystemDto().asEntity())
            val user = req.asUserDto().asEntity(password = req.loginId, encoder)
            user.systems.add(system)
            userRepository.save(user)
            registerCategory(system)
            return true
        }
        return false
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
        val refreshToken = tokenProvider.createRefreshToken()
        userRefreshTokenRepository.findByIdOrNull(userEntity!!.userId)?.updateRefeshToken(refreshToken)
            ?: userRefreshTokenRepository.save(UserRefreshTokenEntity(userEntity,refreshToken))
        return userDto?.asLoginResponse(token,refreshToken)
    }
    fun checkDuplicate(loginId: String, systemName: String) = checkLoginId(loginId) && checkSystemName(systemName)
    fun checkLoginId(loginId: String) = !userRepository.existsByLoginId(loginId)
    fun checkSystemName(systemName: String) =  !systemRepository.existsBySystemName(systemName)
    fun updateUser(req:UpdateRequest) : Boolean
    {
        if(checkDuplicate(req.loginId,req.systemName)){
            val userEntity = userRepository.findByLoginId(req.loginId)
            userEntity?.let {
                req.updateUserEntity(it,encoder)
                updateDBSystem(it,req)
                userRepository.save(it)
            }
            return true
        }
        return false
    }
    fun updateDBSystem(userEntity: UserEntity,req:UpdateRequest)
    {
        val system = userEntity.systems.firstOrNull()
        system?.let {
            it.systemName = req.systemName
            systemRepository.save(system)
        }
    }
    fun addToBlackList(token: String){
        val expireDate = tokenProvider.getExpireDate(token)
        blackListRepository.save(BlackListEntity(token,expireDate))
        blackListRepository.deleteAllByExpireDateBefore(Instant.now())
    }
}