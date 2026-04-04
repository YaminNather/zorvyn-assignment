package com.example.errorhandling

import com.example.sharedkernel.errorhandling.ExceptionMapperRegistry
import com.example.sharedkernel.errorhandling.ProblemJsonException
import com.example.sharedkernel.module.AppModule
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

fun Application.configureProblemJsonGlobalErrorHandler(appModules: List<AppModule>) {
    val mapperRegistry = ExceptionMapperRegistry {
        appModules.forEach { module -> module.errorMappers(this)  }
    }

    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            val problemJsonException = ProblemJsonException(
                type = "validation-failed",
                title = "Request Validation Failed",
                detail = "One or more constraints were violated",
                extensions = mapOf(
                    "reasons" to buildJsonArray { cause.reasons.forEach { add(JsonPrimitive(it)) } }
                ),
                statusCode = HttpStatusCode.BadRequest.value
            )
            call.response.headers.append(HttpHeaders.ContentType, "application/problem+json")
            call.response.status(HttpStatusCode.fromValue(problemJsonException.statusCode))
            call.respond(problemJsonException)
        }

        exception<Throwable> { call, cause ->
            val problemJsonException = mapperRegistry.resolve(cause)
                ?: ProblemJsonException(
                    type = "internal-server-error",
                    title = "An unknown error occurred",
                    detail = cause.message ?: "An unknown error occurred",
                    extensions = mapOf(
                        "message" to JsonPrimitive(cause.message),
                        "stackTrace" to JsonPrimitive(cause.stackTraceToString()),
                    ),
                    statusCode = HttpStatusCode.InternalServerError.value,
                )

            call.response.headers.append(HttpHeaders.ContentType, "application/problem+json")
            call.response.status(HttpStatusCode.fromValue(problemJsonException.statusCode))
            call.respond(problemJsonException)
        }
    }
}