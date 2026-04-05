package com.example.finance

import com.example.finance.domain.record.RecordRepository
import com.example.finance.infrastructure.persistence.ExposedRecordRepository
import com.example.sharedkernel.errorhandling.ExceptionMapperRegistry
import com.example.sharedkernel.errorhandling.ProblemJsonException
import com.example.sharedkernel.module.AppModule
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Routing
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.module.Module

class FinanceModule : AppModule() {
    override fun dependencies(module: Module) = with(module) {
        single<RecordRepository> { ExposedRecordRepository() }
        Unit
    }

    override fun errorMappers(registry: ExceptionMapperRegistry) = with (registry) {
        // Error mappers will be added here
    }

    override fun routes(routing: Routing): Unit = with (routing) {
        // Routes will be added here
    }
}