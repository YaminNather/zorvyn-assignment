package com.example.finance.infrastructure.persistence

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.java.*
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

/**
 * Exposed table definition for financial records.
 * Uses epoch millis for date storage as javaInstant is not resolving in this version.
 */
object RecordsTable : Table("finance_records") {
    val id = javaUUID("id")
    val amount = long("amount")
    val category = varchar("category", 255)
    val dateMillis = datetime("date_millis")
    val description = varchar("description", 1024).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at")
    val deletedAt = datetime("deleted_at").nullable()

    override val primaryKey = PrimaryKey(id)

}
