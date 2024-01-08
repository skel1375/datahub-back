package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import com.knusolution.datahub.domain.UserRepository
import com.knusolution.datahub.security.TokenProvider
import com.knusolution.datahub.system.domain.*
import org.springframework.security.crypto.password.PasswordEncoder

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val baseCategoryRepository: BaseCategoryRepository,
    private val detailCategoryRepository: DetailCategoryRepository,
    private val encoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val userSystemRepository: UserSystemRepository
){
    fun registerUser(req: JoinRequest):Boolean{
        if(checkDuplicate(req.loginId,req.systemName)){
            val system = systemRepository.save(req.asSystemDto().asEntity())
            val user = req.asUserDto().asEntity(password = req.loginId, encoder)
            val admin = findAdmin()
            val adminSystem = UserSystemDto(user = admin, system = system).asEntity()
            val userSystem = UserSystemDto(user = user, system = system).asEntity()
            userRepository.save(user)
            userSystemRepository.save(userSystem)
            userSystemRepository.save(adminSystem)
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
            val userSystems=userSystemRepository.findByUser(it)
            val systemIds = userSystems.map { userSystem ->
                userSystem.system.systemId
            }
            if(encoder.matches(req.password,it.password)) it.asUserDto(systemIds = systemIds) else null
        }
        val token = tokenProvider.createToken("${userDto?.loginId}:${userDto?.role}")
        return userDto?.asLoginResponse(token)
    }
    fun checkDuplicate(loginId: String, systemName: String) = checkLoginId(loginId) && checkSystemName(systemName)
    fun checkLoginId(loginId: String) = !userRepository.existsByLoginId(loginId)
    fun checkSystemName(systemName: String) =  !systemRepository.existsBySystemName(systemName)

    fun updateUser(req:UpdateRequest):Boolean {
        if (checkDuplicate(req.loginId, req.systemName)) {
            val userEntity = userRepository.findByLoginId(req.loginId)
            userEntity?.let {
                req.updateUserEntity(it, encoder)
                updateDBSystem(it, req)
                userRepository.save(it)
            }
            return true
        }
        return false
    }
    fun updateDBSystem(userEntity: UserEntity,req:UpdateRequest)
    {
        val userSystems = userSystemRepository.findByUser(userEntity)
        val system= userSystems[0].system
        system.systemName = req.systemName
        systemRepository.save(system)
    }

    fun findAdmin():UserEntity {
        val admin = userRepository.findByUserId(1L)
        return admin
    }

    fun delUserSystem(systemId:Long)
    {
        val system =systemRepository.findBySystemId(systemId)
        val admin=findAdmin()
        val userSystems= userSystemRepository.findBySystem(system)
        val user = userSystems.firstOrNull { it.user.userId != 1L }?.user
        val userSystem= user?.let { userSystemRepository.findByUserAndSystem(it,system) }
        val adminSystem=userSystemRepository.findByUserAndSystem(admin,system)

        if (userSystem != null) {
            userSystemRepository.delete(userSystem)
            userSystemRepository.delete(adminSystem)
        }
        if (user != null) {
            userRepository.delete(user)
            systemRepository.delete(system)
        }
    }
}