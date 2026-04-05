package com.example.finance

import com.example.finance.application.commands.CreateRecordCommand
import com.example.finance.domain.record.RecordRepository
import com.example.finance.domain.record.exceptions.InvalidCategoryException
import com.example.finance.infrastructure.persistence.ExposedRecordRepository
import com.example.finance.presentation.controllers.record.RecordController
import com.example.sharedkernel.errorhandling.ExceptionMapperRegistry
import com.example.sharedkernel.errorhandling.ProblemJsonException
import com.example.sharedkernel.module.AppModule
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Routing
import org.koin.core.module.Module
import org.koin.ktor.ext.get

/**
 * Main module class for the Finance domain.
 * Coordinates dependency injection, error mapping, and API routing.
 */
class FinanceModule : AppModule() {
    override fun dependencies(module: Module) = with(module) {
        // Infrastructure
        single<RecordRepository> { ExposedRecordRepository() }
        
        // Application
        single { CreateRecordCommand(get()) }
        
        // Presentation
        single { RecordController(get()) }
        
        Unit
    }

    override fun errorMappers(registry: ExceptionMapperRegistry) = with (registry) {
        register<InvalidCategoryException> { e ->
            ProblemJsonException(
                type = "invalid-category",
                title = "Invalid Category",
                detail = e.message ?: "The specified category is invalid.",
                statusCode = HttpStatusCode.BadRequest.value
            )
        }
        Unit
    }

    override fun routes(routing: Routing): Unit = with (routing) {
        get<RecordController>().registerRoutes(this)
    }
}