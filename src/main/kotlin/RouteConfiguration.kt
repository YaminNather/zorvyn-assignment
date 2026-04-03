package com.example

import com.example.sharedkernel.module.AppModule
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRoutes(appModules: List<AppModule>) {
    routing {
        appModules.forEach { module ->
            module.routes(this)
        }
    }
}