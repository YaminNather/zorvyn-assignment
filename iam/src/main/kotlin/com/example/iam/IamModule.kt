package com.example.iam

import com.example.iam.application.commands.CreateUserCommand
import com.example.iam.application.commands.LoginCommand
import com.example.iam.application.commands.ChangeUserRoleCommand
import com.example.iam.application.commands.ChangeUserNameCommand
import com.example.iam.application.queries.user.GetCurrentUserQuery
import com.example.iam.application.queries.user.ListUsersQuery
import com.example.iam.infrastructure.queries.user.ExposedGetCurrentUserQuery
import com.example.iam.infrastructure.queries.user.ExposedListUsersQuery
import com.example.iam.application.exceptions.UserAlreadyExistsException
import com.example.iam.application.commands.setupadmin.SetupAdminCommand
import com.example.iam.application.commands.setupadmin.exceptions.AdminAlreadyExistsException
import com.example.iam.application.exceptions.LastAdminCannotChangeRoleException
import com.example.iam.application.exceptions.UserNotFoundException
import com.example.iam.domain.auth.JwtProvider
import com.example.iam.domain.auth.PasswordHasher
import com.example.iam.domain.role.exceptions.InvalidRoleNameException
import com.example.iam.domain.user.UserRepository
import com.example.iam.domain.user.exceptions.AuthenticationException
import com.example.iam.domain.user.exceptions.InvalidNameException
import com.example.iam.infrastructure.auth.BcryptPasswordHasher
import com.example.iam.infrastructure.auth.JwtTokenProvider
import com.example.iam.infrastructure.persistence.ExposedUserRepository
import com.example.iam.presentation.controllers.auth.AuthController
import com.example.iam.presentation.controllers.user.UserController
import com.example.sharedkernel.errorhandling.ExceptionMapperRegistry
import com.example.sharedkernel.errorhandling.ProblemJsonException
import com.example.sharedkernel.module.AppModule
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
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
            val config = get<ApplicationConfig>()
            JwtTokenProvider(
                secret = config.property("jwt.secret").getString(),
                issuer = config.property("jwt.domain").getString(),
                audience = config.property("jwt.audience").getString()
            )
        }

        // Application Commands
        single { CreateUserCommand(get(), get()) }
        single { LoginCommand(get(), get(), get()) }
        single { SetupAdminCommand(get(), get()) }
        single { ChangeUserRoleCommand(get()) }
        single { ChangeUserNameCommand(get()) }
        single<GetCurrentUserQuery> { ExposedGetCurrentUserQuery() }
        single<ListUsersQuery> { ExposedListUsersQuery() }

        // Controllers
        single { UserController(get(), get(), get(), get(), get(), get()) }


        single { AuthController(get()) }
        Unit
    }

    override fun errorMappers(registry: ExceptionMapperRegistry) = with(registry) {
        register<AuthenticationException> { e ->
            ProblemJsonException(
                type = "authentication-failed",
                title = "Authentication Failed",
                detail = e.message ?: "Invalid credentials",
                statusCode = HttpStatusCode.Unauthorized.value
            )
        }
        register<AdminAlreadyExistsException> { e ->
            ProblemJsonException(
                type = "admin-already-exists",
                title = "Admin Already Exists",
                detail = "Admin setup is not allowed when users already exist in the system.",
                statusCode = HttpStatusCode.Conflict.value
            )
        }
        register<UserNotFoundException> { e ->
            ProblemJsonException(
                type = "user-not-found",
                title = "User Not Found",
                detail = e.message ?: "The requested user was not found.",
                statusCode = HttpStatusCode.NotFound.value
            )
        }
        register<LastAdminCannotChangeRoleException> { e ->
            ProblemJsonException(
                type = "last-admin-role-change",
                title = "Operation Not Permitted",
                detail = e.message ?: "Cannot change the role of the last admin.",
                statusCode = HttpStatusCode.Forbidden.value
            )
        }
        register<InvalidNameException> { e ->
            ProblemJsonException(
                type = "invalid-name",
                title = "Invalid Name",
                detail = e.message ?: "The provided name is invalid.",
                statusCode = HttpStatusCode.BadRequest.value
            )
        }
        register<UserAlreadyExistsException> { e ->
            ProblemJsonException(
                type = "user-already-exists",
                title = "User Already Exists",
                detail = e.message ?: "User with this email already exists.",
                statusCode = HttpStatusCode.Conflict.value
            )
        }

        register<InvalidRoleNameException> { e ->
            ProblemJsonException(
                type = "invalid-role",
                title = "Invalid Role",
                detail = e.message ?: "Invalid role",
                statusCode = HttpStatusCode.BadRequest.value,
            )
        }
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
