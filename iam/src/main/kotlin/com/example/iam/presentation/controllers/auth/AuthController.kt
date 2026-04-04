package com.example.iam.presentation.controllers.auth

import com.example.iam.application.commands.LoginCommand
import com.example.iam.presentation.controllers.auth.models.LoginRequestBody
import com.example.iam.presentation.controllers.auth.models.LoginResponseBody
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

/**
 * Controller to handle authentication-related endpoints.
 */
internal class AuthController(
    private val loginCommand: LoginCommand
) {
    /**
     * Endpoint to handle user login requests.
     * Delegates to the application layer's LoginCommand for core processing and returns the access token.
     */
    private suspend fun login(context: RoutingContext) = with(context) {
        val request = call.receive<LoginRequestBody>()
        
        // Execute the login logic and obtain the generated JWT
        val token = loginCommand.execute(request.username, request.password)
        
        call.respond(HttpStatusCode.OK, LoginResponseBody(token))
    }

    /**
     * Registers the authentication-related routes under the /auth path.
     */
    fun registerRoutes(route: Route) = with(route) {
        route("/auth") {
            post("/login") { login(this) }
        }
    }
}
