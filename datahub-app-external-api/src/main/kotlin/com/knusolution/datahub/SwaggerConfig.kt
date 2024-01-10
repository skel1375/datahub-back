package com.knusolution.datahub

import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod

private const val SECURITY_SCHEME_NAME = "authorization"
@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
            .components(Components()
                    .addSecuritySchemes(SECURITY_SCHEME_NAME, SecurityScheme()
                            .name(SECURITY_SCHEME_NAME)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")))
            .addSecurityItem(SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .info(apiInfo())

    @Bean
    fun globalHeader() = OperationCustomizer{ operation: Operation, _: HandlerMethod ->
        operation.addParametersItem(Parameter()
                .`in`(ParameterIn.HEADER.toString())
                .schema(StringSchema().name("Refresh-Token"))
                .name("Refresh-Token"))
        operation
    }

    private fun apiInfo() = Info()
            .title("Springdoc 테스트")
            .description("Springdoc을 사용한 Swagger UI 테스트")
            .version("1.0.0")
}