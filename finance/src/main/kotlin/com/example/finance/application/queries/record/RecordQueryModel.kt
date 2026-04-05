package com.example.finance.application.queries.record

import kotlin.time.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A read-optimized model for representing financial record data in responses.
 * Decoupled from the domain entity to allow for independent evolution of the API.
 */
@Serializable
data class RecordQueryModel(
    val id: String,
    val amount: Long,
    val category: String,
    val date: Instant, // ISO-8601 string
    val description: String?
)

