package com.example

import com.example.dependencyinjection.configureDependencyInjection
import com.example.errorhandling.configureProblemJsonGlobalErrorHandler
import com.example.finance.FinanceModule
import com.example.iam.IamModule
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.websocket.WebSocketDeflateExtension.Companion.install

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val modules = listOf(FinanceModule(), IamModule())

    configureDependencyInjection(modules)
    configureProblemJsonGlobalErrorHandler(modules)
    configureRoutes(modules)
    configureSecurity()

    install (ContentNegotiation) {
        json()
        json(contentType = ContentType("application", "problem+json"))
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}
