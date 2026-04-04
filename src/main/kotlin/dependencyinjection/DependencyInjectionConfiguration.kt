package com.example.dependencyinjection

import com.example.sharedkernel.module.AppModule
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection(appModules: List<AppModule>) {
    install(Koin) {
        slf4jLogger()

        modules(
            module {
                single { environment.config }
                appModules.forEach { e -> e.dependencies(this@module) }
            }
        )
    }
}
