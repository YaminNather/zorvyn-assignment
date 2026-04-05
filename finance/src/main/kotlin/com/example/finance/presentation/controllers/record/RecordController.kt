package com.example.finance.presentation.controllers.record

import com.example.finance.application.commands.CreateRecordCommand
import com.example.finance.application.commands.UpdateRecordCommand
import com.example.finance.presentation.controllers.record.models.CreateRecordRequestBody
import com.example.finance.presentation.controllers.record.models.CreateRecordResponseBody
import com.example.finance.presentation.controllers.record.models.UpdateRecordRequestBody
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import com.example.sharedkernel.authorization.Permission
import com.example.sharedkernel.authorization.withPermission
import java.time.Instant
import java.util.*

/**
 * Controller to handle API requests related to financial records.
 * Uses JWT principal to identify the current user.
 */
internal class RecordController(
    private val createRecordCommand: CreateRecordCommand,
    private val updateRecordCommand: UpdateRecordCommand
) {
    /**
     * Handles the creation of a financial record for the authenticated user.
     * Parses the request body and delegates to CreateRecordCommand.
     */
    private suspend fun createRecord(context: RoutingContext) = with(context) {
        val principal = call.principal<JWTPrincipal>()
        val userIdString = principal?.payload?.subject ?: return@with call.respond(HttpStatusCode.Unauthorized)
        val userId = UUID.fromString(userIdString)

        val request = call.receive<CreateRecordRequestBody>()
        
        val id = createRecordCommand.execute(
            userId = userId,
            amount = request.amount,
            category = request.category,
            date = Instant.parse(request.date),
            description = request.description
        )
        
        call.respond(HttpStatusCode.Created, CreateRecordResponseBody(id.toString()))
    }

    /**
     * Handles the update of an existing financial record.
     */
    private suspend fun updateRecord(context: RoutingContext) = with(context) {
        val principal = call.principal<JWTPrincipal>()
        val userIdString = principal?.payload?.subject ?: return@with call.respond(HttpStatusCode.Unauthorized)
        val userId = UUID.fromString(userIdString)

        val idParam = call.parameters["id"] ?: return@with call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing record ID"))
        val recordId = try {
            UUID.fromString(idParam)
        } catch (e: Exception) {
            return@with call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid record ID format"))
        }

        val request = call.receive<UpdateRecordRequestBody>()
        
        updateRecordCommand.execute(
            userId = userId,
            recordId = recordId,
            amount = request.amount,
            category = request.category,
            date = request.date?.let { Instant.parse(it) },
            description = request.description
        )
        
        call.respond(HttpStatusCode.NoContent)
    }


    /**
     * Registers financial record related routes under /finance/records.
     * Implements request validation for robust API interactions.
     */
    fun registerRoutes(route: Route) = with(route) {
        authenticate {
            withPermission(Permission.RECORDS_MANAGE) {
                route("/finance/records") {
                    install(RequestValidation) {
                        validate<CreateRecordRequestBody> { body ->
                            if (body.category.isBlank()) ValidationResult.Invalid("Category cannot be blank")
                            else if (body.amount == 0L) ValidationResult.Invalid("Amount cannot be zero")
                            else try {
                                Instant.parse(body.date)
                                ValidationResult.Valid
                            } catch (e: Exception) {
                                ValidationResult.Invalid("Invalid date format. Expected ISO-8601.")
                            }
                        }
                    }

                    post { createRecord(this) }
                    
                    patch("/{id}") { updateRecord(this) }
                }
            }
        }
    }
}
