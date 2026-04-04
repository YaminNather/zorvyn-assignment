package com.example.finance.presentation.controllers.transaction
 
import com.example.finance.application.commands.CreateTransactionCommand

import com.example.finance.presentation.controllers.transaction.models.CreateTransactionRequestBody
import com.example.finance.presentation.controllers.transaction.models.CreateTransactionResponseBody
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.http.HttpStatusCode

internal class TransactionController(
    private val createTransactionCommand: CreateTransactionCommand,
) {
    private suspend fun createTransaction(context: RoutingContext) = with(context) {
//        val request = call.receive<CreateTransactionRequestBody>()
//        val id = createTransactionCommand.execute(request.amount, request.recordType)
//
//        call.respond(HttpStatusCode.Created, CreateTransactionResponseBody(id))
    }

    fun registerRoutes(route: Route) = with(route) {
        route("/transaction") {
            post { createTransaction(this) }
        }
    }
}