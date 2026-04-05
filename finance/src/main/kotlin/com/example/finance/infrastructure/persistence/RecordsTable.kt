package com.example.finance.infrastructure.persistence

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.java.*

/**
 * Exposed table definition for financial records.
 * Uses epoch millis for date storage as javaInstant is not resolving in this version.
 */
object RecordsTable : Table("finance_records") {
    val id = javaUUID("id")
    val userId = javaUUID("user_id")
    val amount = long("amount")
    val category = varchar("category", 255)
    val dateMillis = long("date_millis")
    val description = varchar("description", 1024).nullable()
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}
