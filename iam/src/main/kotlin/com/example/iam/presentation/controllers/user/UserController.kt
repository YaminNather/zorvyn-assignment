package com.example.iam.presentation.controllers.user

import com.example.iam.application.commands.CreateUserCommand
import com.example.iam.application.commands.setupadmin.SetupAdminCommand
import com.example.iam.application.commands.ChangeUserRoleCommand
import com.example.iam.application.commands.ChangeUserNameCommand
import com.example.iam.application.queries.user.GetCurrentUserQuery
import com.example.iam.application.queries.user.ListUsersQuery
import com.example.iam.presentation.controllers.user.models.CreateUserRequestBody
import com.example.iam.presentation.controllers.user.models.ChangeUserRoleRequestBody
import com.example.iam.presentation.controllers.user.models.ChangeUserNameRequestBody
import com.example.iam.presentation.controllers.user.models.CreateUserResponseBody
import com.example.sharedkernel.authorization.Permission
import com.example.sharedkernel.authorization.withPermission
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import com.example.sharedkernel.errorhandling.RequestValidationException
import com.example.iam.domain.user.exceptions.AuthenticationException
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import java.util.UUID

internal class UserController(
    private val createUserCommand: CreateUserCommand,
    private val setupAdminCommand: SetupAdminCommand,
    private val changeUserRoleCommand: ChangeUserRoleCommand,
    private val changeUserNameCommand: ChangeUserNameCommand,
    private val getCurrentUserQuery: GetCurrentUserQuery,
    private val listUsersQuery: ListUsersQuery
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
        val id = setupAdminCommand.execute()
        call.respond(HttpStatusCode.Created, mapOf("message" to "Admin created", "id" to id.toString()))
    }

    /**
     * Lists all registered users. Requires USERS_MANAGE permission.
     */
    private suspend fun listUsers(context: RoutingContext) = with(context) {
        val users = listUsersQuery.execute()
        call.respond(HttpStatusCode.OK, users)
    }

    /**
     * Retrieves the details of the currently authenticated user based on their JWT.

     */
    private suspend fun me(context: RoutingContext) = with(context) {
        val principal = call.principal<JWTPrincipal>() ?: throw AuthenticationException()
        val userId = UUID.fromString(principal.payload.subject)
        
        val details = getCurrentUserQuery.execute(userId)
        call.respond(HttpStatusCode.OK, details)
    }

    /**
     * Handles the request to change a user's name.
     */
    private suspend fun changeUserName(context: RoutingContext) = with(context) {
        val idParam = call.parameters["id"] ?: throw RequestValidationException("Missing user ID")
        val userId = UUID.fromString(idParam)
        val request = call.receive<ChangeUserNameRequestBody>()
        
        changeUserNameCommand.execute(userId, request.name)
        call.respond(HttpStatusCode.NoContent)
    }

    /**
     * Handles the request to change a user's assigned role.
     */
    private suspend fun changeUserRole(context: RoutingContext) = with(context) {
        val idParam = call.parameters["id"] ?: throw RequestValidationException("Missing user ID")
        val userId = UUID.fromString(idParam)
        val request = call.receive<ChangeUserRoleRequestBody>()
        
        changeUserRoleCommand.execute(userId, request.roleName)
        call.respond(HttpStatusCode.NoContent)
    }



    /**
     * Registers user-related routes under the /user path.
     */
    fun registerRoutes(route: Route) = with(route) {
        route("/setup") {
            post("/admin") { createAdminIfNone(this) }
        }
        authenticate {
            route("/user") {
                get("/me") { me(this) }
            }
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
                        validate<ChangeUserRoleRequestBody> { body ->
                            if (body.roleName.isBlank()) ValidationResult.Invalid("Role name cannot be blank")
                            else ValidationResult.Valid
                        }
                        validate<ChangeUserNameRequestBody> { body ->
                            if (body.name.isBlank()) ValidationResult.Invalid("Name cannot be blank")
                            else ValidationResult.Valid
                        }
                    }
                    post { createUser(this) }
                    get { listUsers(this) }

                    patch("/{id}/name") { changeUserName(this) }

                    patch("/{id}/role") { changeUserRole(this) }
                }
            }
        }
    }
}
