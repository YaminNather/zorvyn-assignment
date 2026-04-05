package com.example

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging

internal fun Application.configureCallLogging() {
    install(CallLogging) {

    }
}