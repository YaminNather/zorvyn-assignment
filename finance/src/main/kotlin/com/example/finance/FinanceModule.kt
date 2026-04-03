package com.example.finance

import com.example.finance.presentation.controllers.transaction.TransactionController
import com.example.sharedkernel.errorhandling.ExceptionMapperRegistry
import com.example.sharedkernel.errorhandling.ProblemJsonException
import com.example.sharedkernel.module.AppModule
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.module.Module
import org.koin.ktor.ext.inject

class FinanceModule : AppModule() {
    override fun dependencies(module: Module) = with(module) {

    }

    override fun errorMappers(registry: ExceptionMapperRegistry) = with (registry) {
        register<MyCustomException> { _ ->
            ProblemJsonException(
                type = "my-custom-exception",
                title = "My Custom Exception",
                detail = "This is a custom exception that I am using for trying out something",
                extensions = mapOf(
                    "property0" to JsonPrimitive("Value0"),
                    "property1" to JsonPrimitive(1)
                ),
                statusCode = HttpStatusCode.BadRequest.value,
            )
        }
    }

    override fun routes(routing: Routing): Unit = with (routing) {
        val transactionController: TransactionController by inject()

        route("/finance") {
            transactionController.registerRoutes(this@route)
        }
    }
}

internal class MyCustomException : Exception()