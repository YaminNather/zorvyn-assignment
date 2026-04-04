package com.example.iam.presentation.controllers.user

import com.example.iam.application.commands.CreateUserCommand
import com.example.iam.presentation.controllers.user.models.CreateUserRequestBody
import com.example.iam.presentation.controllers.user.models.CreateUserResponseBody
import com.example.sharedkernel.authorization.Permission
import com.example.sharedkernel.authorization.withPermission
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

import com.example.iam.domain.user.UserRepository

/**
 * Controller to handle API requests related to user management.
 * Encapsulates routing and mapping for User creation.
 */
internal class UserController(
    private val createUserCommand: CreateUserCommand,
    private val userRepository: UserRepository
) {
    /**
     * Handles the user creation post request.
     * Parses the request body and delegates to CreateUserCommand.
     */
    private suspend fun createUser(context: RoutingContext) = with(context) {
        val request = call.receive<CreateUserRequestBody>()
        
        // Execute the command and return the generated ID.
        // Hashing now happens at the application layer inside CreateUserCommand.
        val id = createUserCommand.execute(
            name = request.name,
            email = request.email,
            password = request.password, 
            roleName = request.roleName
        )
        
        call.respond(HttpStatusCode.Created, CreateUserResponseBody(id.toString()))
    }

    /**
     * Unprotected endpoint to initialize the admin user if the database is empty.
     */
    private suspend fun createAdminIfNone(context: RoutingContext) = with(context) {
        if (userRepository.count() == 0L) {
            val id = createUserCommand.execute(
                name = "admin",
                email = "admin@example.com",
                password = "admin",
                roleName = "Admin" // Make sure "Admin" matches Role.ADMIN.name logic
            )
            call.respond(HttpStatusCode.Created, mapOf("message" to "Admin created", "id" to id.toString()))
        } else {
            call.respond(HttpStatusCode.Conflict, mapOf("message" to "Users already exist"))
        }
    }

    /**
     * Registers user-related routes under the /user path.
     */
    fun registerRoutes(route: Route) = with(route) {
        route("/setup") {
            post("/admin") { createAdminIfNone(this) }
        }
        authenticate {
            withPermission(Permission.USERS_MANAGE) {
                route("/user") {
                    install(RequestValidation) {
                        validate<CreateUserRequestBody> { body ->
                            if (body.name.isBlank()) ValidationResult.Invalid("Name cannot be blank")
                            else if (body.email.isBlank() || !body.email.contains("@")) ValidationResult.Invalid("Valid email is required")
                            else if (body.password.length < 6) ValidationResult.Invalid("Password must be at least 6 characters long")
                            else if (body.roleName.isBlank()) ValidationResult.Invalid("Role name cannot be blank")
                            else ValidationResult.Valid
                        }
                    }
                    post { createUser(this) }
                }
            }
        }
    }
}
