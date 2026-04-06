package com.example.iam.presentation.controllers.auth

import com.example.iam.application.commands.LoginCommand
import com.example.iam.domain.validations.validateEmail
import com.example.iam.domain.validations.validatePassword
import com.example.iam.presentation.controllers.auth.models.LoginRequestBody
import com.example.iam.presentation.controllers.auth.models.LoginResponseBody
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

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
        val token = loginCommand.execute(request.email, request.password)
        
        call.respond(HttpStatusCode.OK, LoginResponseBody(token))
    }

    /**
     * Registers the authentication-related routes under the /auth path.
     */
    fun registerRoutes(route: Route) = with(route) {
        route("/auth") {
            install(RequestValidation) {
                validate<LoginRequestBody> { body ->
                    val emailError = validateEmail(body.email)
                    val passwordError = validatePassword(body.password)

                    if (emailError != null) ValidationResult.Invalid("Invalid email, $emailError")
                    else if (passwordError != null) ValidationResult.Invalid("Invalid password, $passwordError")
                    else ValidationResult.Valid
                }
            }
            post("/login") { login(this) }
        }
    }
}
