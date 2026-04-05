package com.example.iam.infrastructure.persistence

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.java.*

/**
 * Exposed table definition for the "users" table using Exposed 1.0.0 V1 packages.
 * Maps User domain entity fields to database columns.
 */
object UsersTable : Table("iam_users") {
    val id = javaUUID("id")
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50)
    val status = varchar("status", 50)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}
