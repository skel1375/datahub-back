package com.knusolution.datahub

import com.knusolution.datahub.domain.*
import com.knusolution.datahub.system.domain.SystemDto
import com.knusolution.datahub.system.domain.SystemRepository
import com.knusolution.datahub.system.domain.asEntity
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserInitDataLoader(
    private val userRepository: UserRepository,
    private val userSystemRepository: UserSystemRepository,
    private val systemRepository: SystemRepository,
    private val encoder: PasswordEncoder
) : CommandLineRunner{
    override fun run(vararg args: String?) {
        val user = UserDto(
            loginId = "admin",
            companyName = "",
            developerName = "",
            contactNum = "",
            department = "",
            departmentName = "",
            role = Role.ADMIN
        )

        val system = SystemDto(
            systemName = "경상북도 도청",
            isSystem= false
        )


        if (!userRepository.existsByLoginId("admin")) {
            val savedUser = userRepository.save(user.asEntity(password = "admin", encoder))
            val savedSystem = systemRepository.save(system.asEntity())
            val userSystem = UserSystemDto(
                user = savedUser,
                system = savedSystem
            )
            userSystemRepository.save(userSystem.asEntity())
        }
    }
}