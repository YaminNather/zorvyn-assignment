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

/**
 * Controller to handle API requests related to user management.
 * Encapsulates routing and mapping for User creation.
 */
internal class UserController(
    private val createUserCommand: CreateUserCommand
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
            username = request.username,
            email = request.email,
            password = request.password, 
            roleName = request.roleName
        )
        
        call.respond(HttpStatusCode.Created, CreateUserResponseBody(id.toString()))
    }

    /**
     * Registers user-related routes under the /user path.
     */
    fun registerRoutes(route: Route) = with(route) {
        authenticate {
            withPermission(Permission.USERS_MANAGE) {
                route("/user") {
                    post { createUser(this) }
                }
            }
        }
    }
}
