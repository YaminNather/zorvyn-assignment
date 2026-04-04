package com.example.iam

import com.example.iam.application.commands.CreateUserCommand
import com.example.iam.application.commands.LoginCommand
import com.example.iam.domain.auth.JwtProvider
import com.example.iam.domain.auth.PasswordHasher
import com.example.iam.domain.user.UserRepository
import com.example.iam.infrastructure.auth.BcryptPasswordHasher
import com.example.iam.infrastructure.auth.JwtTokenProvider
import com.example.iam.infrastructure.persistence.ExposedUserRepository
import com.example.iam.presentation.controllers.auth.AuthController
import com.example.iam.presentation.controllers.user.UserController
import com.example.sharedkernel.errorhandling.ExceptionMapperRegistry
import com.example.sharedkernel.errorhandling.ProblemJsonException
import com.example.sharedkernel.module.AppModule
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import org.koin.core.module.Module
import org.koin.ktor.ext.inject

/**
 * IAM Module configuration.
 * Registers dependencies, routes, and error mappers for the Identity & Access Management module.
 */
class IamModule : AppModule() {

    override fun dependencies(module: Module) = with(module) {
        // Infrastructure
        single<UserRepository> { ExposedUserRepository() }
        single<PasswordHasher> { BcryptPasswordHasher() }
        single<JwtProvider> {
            val config = get<io.ktor.server.config.ApplicationConfig>()
            JwtTokenProvider(
                secret = config.property("jwt.secret").getString(),
                issuer = config.property("jwt.domain").getString(),
                audience = config.property("jwt.audience").getString()
            )
        }

        // Application Commands
        single { CreateUserCommand(get(), get()) }
        single { LoginCommand(get(), get(), get()) }

        // Controllers
        single { UserController(get()) }
        single { AuthController(get()) }
        Unit
    }

    override fun errorMappers(registry: ExceptionMapperRegistry) = with(registry) {
        // Register IAM specific exception mappers here if needed
        register<com.example.iam.domain.user.exceptions.AuthenticationException> { e ->
            ProblemJsonException(
                type = "authentication-failed",
                title = "Authentication Failed",
                detail = e.message ?: "Invalid credentials",
                statusCode = HttpStatusCode.Unauthorized.value
            )
        }
        Unit
    }

    override fun routes(routing: Routing): Unit = with(routing) {
        val userController: UserController by inject()
        val authController: AuthController by inject()

        route("/iam") {
            userController.registerRoutes(this@route)
            authController.registerRoutes(this@route)
        }
    }
}
