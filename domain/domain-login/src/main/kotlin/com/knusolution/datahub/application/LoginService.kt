package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import com.knusolution.datahub.domain.UserRepository
import com.knusolution.datahub.security.TokenProvider
import com.knusolution.datahub.security.domain.*
import com.knusolution.datahub.system.domain.*
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.NoSuchElementException

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val blackListRepository: BlackListRepository,
    private val baseCategoryRepository: BaseCategoryRepository,
    private val detailCategoryRepository: DetailCategoryRepository,
    private val encoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val userSystemRepository: UserSystemRepository,
    private val userRefreshTokenRepository: UserRefreshTokenRepository
){
    //유저와 시스템정보를 받아 시스템등록과 유저 등록
    fun registerUser(req: JoinRequest):Boolean{
        if(checkDuplicate(req.loginId,req.systemName)){
            val system = systemRepository.save(req.asSystemDto().asEntity())
            val user = req.asUserDto().asEntity(password = req.loginId, encoder)
            val admin = userRepository.findByRole(Role.ADMIN)
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
    //시스템등록시 하위 시스템 등록
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

    //로그인 기능, 토큰을 발급 받는다.
    fun loginUser(req: LoginRequest): LoginResponse?
    {
        val userEntity = userRepository.findByLoginId(req.loginId) ?: throw(IllegalArgumentException("존재하지 않는 ID입니다."))
        val userDto = userEntity.let { it ->
            val userSystems=userSystemRepository.findByUser(it)
            val systemIds = userSystems.map { userSystem ->
                userSystem.system.systemId
            }
            if(encoder.matches(req.password,it.password)) it.asUserDto(systemIds = systemIds)
            else throw(IllegalArgumentException("비밀번호가 일치하지 않습니다."))
        }
        val token = tokenProvider.createToken("${userEntity.userId}:${userEntity.role}")
        val refreshToken = tokenProvider.createRefreshToken()
        val existingEntity = userRefreshTokenRepository.findByIdOrNull(userEntity.userId)
        if (existingEntity != null) {
            // 이미 리프레시 토큰이 존재하면 업데이트
            existingEntity.updateRefreshToken(refreshToken)
            existingEntity.updateRefreshToken(refreshToken)
            userRefreshTokenRepository.save(existingEntity)
        } else {
            // 리프레시 토큰이 존재하지 않으면 새로 생성하여 저장
            userRefreshTokenRepository.save(UserRefreshTokenEntity(userEntity, refreshToken))
        }
        return userDto.asLoginResponse(token,refreshToken)
    }

    //아이디 중복, 시스템 이름 중복 검사
    fun checkDuplicate(loginId: String, systemName: String) = checkLoginId(loginId) && checkSystemNameOnJoin(systemName)
    //아이디 중복 검사
    fun checkLoginId(loginId: String) = !userRepository.existsByLoginId(loginId)
    //시스템 등록시 시스템 이름 중복 검사
    fun checkSystemNameOnJoin(systemName: String) = !systemRepository.existsBySystemName(systemName)
    //정보 수정 시 시스템 이름 중복 검사, 현재 정보와 같아도 중복 허용하기 위하여 따로 설정
    fun checkSystemNameOnUpdate(loginId: String, systemName: String) :Boolean {
        val user = userRepository.findByLoginId(loginId) ?: throw NoSuchElementException("유저를 찾을 수 없습니다.")
        val curSystemName = userSystemRepository.findByUser(user).first().system.systemName
        return !systemRepository.existsBySystemName(systemName) || curSystemName == systemName
    }
    //유저 정보 수정
    fun updateUser(req:UpdateRequest):Boolean {
        if (checkSystemNameOnUpdate(req.loginId,req.systemName)) {
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
    //시스템정보수정
    fun updateDBSystem(userEntity: UserEntity,req:UpdateRequest)
    {
        val userSystems = userSystemRepository.findByUser(userEntity)
        val system= userSystems[0].system
        system.systemName = req.systemName
        systemRepository.save(system)
    }
    //유저와 시스템정보 삭제
    fun delUserSystem(systemId:Long)
    {
        val system =systemRepository.findBySystemId(systemId)
        val admin= userRepository.findByRole(Role.ADMIN)
        val userSystems= userSystemRepository.findBySystem(system)
        val user = userSystems.firstOrNull { it.user.role != Role.ADMIN }?.user
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

    //로그아웃한 유저의 토큰을 블랙리스트에 추가
    @Transactional
    fun logoutUser(token: String, loginId: String){
        userRefreshTokenRepository.deleteById(userRepository.findByLoginId(loginId)!!.userId)
        try {
            val expireDate = tokenProvider.getExpireDate(token)
            blackListRepository.save(BlackListEntity(token,expireDate))
            blackListRepository.deleteAllByExpireDateBefore(Date()) // 유효기간 만료된 토큰은 더이상 필요없으므로 블랙리스트에서 삭제
        } catch (e:ExpiredJwtException) {
            ResponseEntity.status(HttpStatus.OK).body("이미 만료된 토큰입니다.")
        }
    }
    //시스템과 연결된 유저정보전달
    fun getUserInfor(systemId: Long): InfoResponse
    {
        val userSystems = userSystemRepository.findBySystemSystemId(systemId)
        val user = userSystems.first{ it.user.role != Role.ADMIN }.user.asUserDto()
        val systemName= systemRepository.findBySystemId(systemId).systemName
        return user.asInfoResponse(systemName)
    }
}