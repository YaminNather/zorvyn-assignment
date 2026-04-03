package com.example.finance.presentation.controllers.transaction

import com.example.finance.application.commands.CreateTransactionCommand
import com.example.finance.presentation.controllers.transaction.models.CreateTransactionResponseBody
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.post
import io.ktor.server.routing.route

internal class TransactionController(
    private val createTransactionCommand: CreateTransactionCommand,
) {
    private suspend fun createTransaction(context: RoutingContext) = with(context) {
        val id = createTransactionCommand.execute()

        call.respond(CreateTransactionResponseBody(id))
    }

    fun registerRoutes(route: Route) = with(route) {
        route("/transaction") {
            post { createTransaction(this) }
        }
    }
}