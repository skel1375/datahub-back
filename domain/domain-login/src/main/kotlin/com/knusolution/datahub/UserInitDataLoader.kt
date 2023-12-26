package com.knusolution.datahub

import com.knusolution.datahub.domain.Role
import com.knusolution.datahub.domain.UserDto
import com.knusolution.datahub.domain.UserRepository
import com.knusolution.datahub.domain.asEntity
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class UserInitDataLoader(
    private val userRepository: UserRepository,
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
        userRepository.save(user.asEntity(password = "admin"))
    }
}