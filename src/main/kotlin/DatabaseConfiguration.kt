package com.example

import com.example.finance.infrastructure.persistence.RecordsTable
import com.example.iam.infrastructure.persistence.UsersTable
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

/**
 * Initializes the database connection and performs schema migrations for all modules.
 * Uses Exposed v1.0 with R2DBC support as requested.
 */
suspend fun Application.configureDatabase() {
    val dbUrl = environment.config.property("db.url").getString()
    val dbUser = environment.config.property("db.user").getString()
    val dbPassword = environment.config.property("db.password").getString()

    // Connect to the database using R2DBC
    R2dbcDatabase.connect(
        url = dbUrl,
        user = dbUser,
        password = dbPassword,
    )

    initializeSchema()
}

/**
 * Helper function to run initial DDL across all modules.
 */
suspend fun initializeSchema() = suspendTransaction {
    SchemaUtils.create(UsersTable)
    SchemaUtils.create(RecordsTable)
}
