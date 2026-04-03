package com.example.sharedkernel.module

import com.example.sharedkernel.errorhandling.ExceptionMapperRegistry
import com.example.sharedkernel.errorhandling.ProblemJsonException
import io.ktor.server.routing.Routing
import org.koin.core.module.Module
import org.koin.dsl.ModuleDeclaration

open class AppModule {
    open fun dependencies(module: Module) {}

    open fun routes(routing: Routing) {}

    open fun errorMappers(registry: ExceptionMapperRegistry) {}
}