package com.example.finance.presentation.controllers.record.models

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a new financial record.
 * Amount is expected in Indian Paisa (e.g. 10000 for Rs 100.00).
 */
@Serializable
data class CreateRecordRequestBody(
    val amount: Long,
    val category: String,
    val date: String, // Expect ISO-8601 string, e.g. "2024-03-20T10:00:00Z"
    val description: String? = null
)

/**
 * Response DTO returned after successful record creation.
 */
@Serializable
data class CreateRecordResponseBody(
    val id: String
)
