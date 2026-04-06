package com.example.errorhandling

import com.example.sharedkernel.errorhandling.ExceptionMapperRegistry
import com.example.sharedkernel.errorhandling.ProblemJsonException
import com.example.sharedkernel.module.AppModule
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import io.ktor.server.plugins.requestvalidation.RequestValidationException as KtorRequestValidationException
import com.example.sharedkernel.errorhandling.RequestValidationException as CustomRequestValidationException
import kotlinx.serialization.json.JsonPrimitive

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureProblemJsonGlobalErrorHandler(appModules: List<AppModule>) {
    val mapperRegistry = ExceptionMapperRegistry {
        appModules.forEach { module -> module.errorMappers(this)  }
    }

    install(StatusPages) {
        exception<KtorRequestValidationException> { call, cause ->
            val problemJsonException = ProblemJsonException(
                type = "validation-failed",
                title = "Request Validation Failed",
                detail = "One or more constraints were violated",
                extensions = mapOf(
                    "reasons" to buildJsonArray { cause.reasons.forEach { add(JsonPrimitive(it)) } }
                ),
                statusCode = HttpStatusCode.BadRequest.value
            )

            call.respondWithProblemJsonException(problemJsonException)
        }

        exception<CustomRequestValidationException> { call, cause ->
            val problemJsonException = ProblemJsonException(
                type = "validation-failed",
                title = "Request Validation Failed",
                detail = cause.message ?: "The request contains invalid data",
                statusCode = HttpStatusCode.BadRequest.value
            )

            call.respondWithProblemJsonException(problemJsonException)
        }

        exception<BadRequestException> { call, cause ->
            val jsonConvertException = cause.cause as JsonConvertException
            val problemJsonException = if (jsonConvertException.cause is MissingFieldException) {
                val missingFieldsException = jsonConvertException.cause as MissingFieldException
                val missingFields = missingFieldsException.missingFields

                 ProblemJsonException(
                    type = "missing-fields",
                    title = "Missing Request Fields",
                    detail = "Required fields missing in request body",
                    extensions = mapOf(
                        "missingFields" to JsonArray(missingFields.map { JsonPrimitive(it) }),
                    ),
                    statusCode = HttpStatusCode.BadRequest.value,
                )
            }
            else {
                cause.toProblemJsonException()
            }

            call.respondWithProblemJsonException(problemJsonException)
        }

        exception<Throwable> { call, cause ->
            val problemJsonException = mapperRegistry.resolve(cause)
                ?: cause.toProblemJsonException()

            call.respondWithProblemJsonException(problemJsonException)
        }
    }
}

private suspend fun ApplicationCall.respondWithProblemJsonException(exception: ProblemJsonException) {
    response.headers.append(HttpHeaders.ContentType, "application/problem+json")
    response.status(HttpStatusCode.fromValue(exception.statusCode))
    respond(exception)
}

private fun Throwable.toProblemJsonException(): ProblemJsonException {
    return ProblemJsonException(
        type = "internal-server-error",
        title = "An unknown error occurred",
        detail = message ?: "An unknown error occurred",
        extensions = mapOf(
            "message" to JsonPrimitive(message),
            "stackTrace" to JsonPrimitive(stackTraceToString()),
        ),
        statusCode = HttpStatusCode.InternalServerError.value,
    )
}