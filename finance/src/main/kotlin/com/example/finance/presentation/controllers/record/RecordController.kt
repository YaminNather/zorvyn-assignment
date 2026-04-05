package com.example.finance.presentation.controllers.record

import com.example.finance.application.commands.CreateRecordCommand
import com.example.finance.application.commands.UpdateRecordCommand
import com.example.finance.application.commands.DeleteRecordCommand
import com.example.finance.application.queries.record.GetRecordQuery
import com.example.finance.application.queries.record.GetSummaryQuery
import com.example.finance.application.queries.record.ListRecordsQuery

import com.example.finance.presentation.controllers.record.models.CreateRecordRequestBody


import com.example.finance.presentation.controllers.record.models.CreateRecordResponseBody
import com.example.finance.presentation.controllers.record.models.UpdateRecordRequestBody
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import com.example.sharedkernel.authorization.Permission
import com.example.sharedkernel.authorization.withPermission
import com.example.sharedkernel.errorhandling.RequestValidationException
import java.util.*




/**
 * Controller to handle API requests related to financial records.
 * Uses JWT principal to identify the current user.
 */
internal class RecordController(
    private val createRecordCommand: CreateRecordCommand,
    private val updateRecordCommand: UpdateRecordCommand,
    private val deleteRecordCommand: DeleteRecordCommand,
    private val getRecordQuery: GetRecordQuery,
    private val listRecordsQuery: ListRecordsQuery,
    private val getSummaryQuery: GetSummaryQuery
) {

    /**
     * Handles the creation of a financial record for the authenticated user.
     * Parses the request body and delegates to CreateRecordCommand.
     */
    private suspend fun createRecord(context: RoutingContext) = with(context) {
        val request = call.receive<CreateRecordRequestBody>()
        
        val id = createRecordCommand.execute(
            amount = request.amount,
            category = request.category,
            date = request.date,

            description = request.description
        )

        
        call.respond(HttpStatusCode.Created, CreateRecordResponseBody(id.toString()))
    }

    /**
     * Handles the update of an existing financial record.
     */
    private suspend fun updateRecord(context: RoutingContext) = with(context) {
        val idParam = call.parameters["id"] ?: throw RequestValidationException("Missing record ID")
        val recordId = try {
            UUID.fromString(idParam)
        } catch (e: Exception) {
            throw RequestValidationException("Invalid record ID format")
        }


        val request = call.receive<UpdateRecordRequestBody>()
        
        updateRecordCommand.execute(
            recordId = recordId,
            amount = request.amount,
            category = request.category,
            date = request.date,

            description = request.description
        )

        
        call.respond(HttpStatusCode.NoContent)
    }

    /**
     * Handles the deletion of a financial record.
     */
    private suspend fun deleteRecord(context: RoutingContext) = with(context) {
        val idParam = call.parameters["id"] ?: throw RequestValidationException("Missing record ID")
        val recordId = try {
            UUID.fromString(idParam)
        } catch (e: Exception) {
            throw RequestValidationException("Invalid record ID format")
        }


        deleteRecordCommand.execute(recordId)
        
        call.respond(HttpStatusCode.NoContent)
    }    /**
     * Handles the retrieval of a single financial record.
     */
    private suspend fun getRecord(context: RoutingContext) = with(context) {
        val idParam = call.parameters["id"] ?: throw RequestValidationException("Missing record ID")
        val recordId = try {
            UUID.fromString(idParam)
        } catch (e: Exception) {
            throw RequestValidationException("Invalid record ID format")
        }


        val record = getRecordQuery.execute(recordId)
        
        call.respond(HttpStatusCode.OK, record)
    }

    /**
     * Handles listing of records with filtering and pagination.
     */
    private suspend fun listRecords(context: RoutingContext) = with(context) {
        val queryParams = call.request.queryParameters
        
        val minAmount = queryParams["minAmount"]?.toLongOrNull()
        val maxAmount = queryParams["maxAmount"]?.toLongOrNull()
        val categories = queryParams.getAll("category") // Get multiple if provided
        val startDate = queryParams["startDate"]?.let { try { kotlin.time.Instant.parse(it) } catch (e: Exception) { null } }
        val endDate = queryParams["endDate"]?.let { try { kotlin.time.Instant.parse(it) } catch (e: Exception) { null } }




        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            throw RequestValidationException("minAmount cannot be greater than maxAmount")
        }
        if (startDate != null && endDate != null && startDate > endDate) {
            throw RequestValidationException("startDate cannot be after endDate")
        }



        val page = (queryParams["page"]?.toIntOrNull() ?: 1).coerceAtLeast(1)
        val pageSize = (queryParams["pageSize"]?.toIntOrNull() ?: 20).coerceIn(1, 100)
        
        val response = listRecordsQuery.execute(
            minAmount = minAmount,
            maxAmount = maxAmount,
            categories = categories,
            startDate = startDate,
            endDate = endDate,
            page = page,
            pageSize = pageSize
        )

        
        call.respond(HttpStatusCode.OK, response)
    }

    /**
     * Handles retrieval of a financial summary with filtering.
     */
    private suspend fun getSummary(context: RoutingContext) = with(context) {
        val queryParams = call.request.queryParameters
        
        val minAmount = queryParams["minAmount"]?.toLongOrNull()
        val maxAmount = queryParams["maxAmount"]?.toLongOrNull()
        val categories = queryParams.getAll("category")
        val startDate = queryParams["startDate"]?.let { try { kotlin.time.Instant.parse(it) } catch (e: Exception) { null } }
        val endDate = queryParams["endDate"]?.let { try { kotlin.time.Instant.parse(it) } catch (e: Exception) { null } }

        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            throw RequestValidationException("minAmount cannot be greater than maxAmount")
        }

        if (startDate != null && endDate != null && startDate > endDate) {
            throw RequestValidationException("startDate cannot be after endDate")
        }


        val summary = getSummaryQuery.execute(

            minAmount = minAmount,
            maxAmount = maxAmount,
            categories = categories,
            startDate = startDate,
            endDate = endDate
        )

        
        call.respond(HttpStatusCode.OK, summary)
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
                            else ValidationResult.Valid
                        }
                        validate<UpdateRecordRequestBody> { body ->
                            if (body.category?.isBlank() == true) ValidationResult.Invalid("Category cannot be blank")
                            else if (body.amount == 0L) ValidationResult.Invalid("Amount cannot be zero")
                            else ValidationResult.Valid
                        }


                    }

                    post { createRecord(this) }
                    
                    patch("/{id}") { updateRecord(this) }
                    
                    delete("/{id}") { deleteRecord(this) }
                }
            }
            
            withPermission(Permission.RECORDS_VIEW) {
                route("/finance/records") {
                    get { listRecords(this) }
                    get("/{id}") { getRecord(this) }
                }
            }

            withPermission(Permission.DASHBOARD_VIEW) {
                get("/finance/summary") { getSummary(this) }
            }
        }
    }
}
