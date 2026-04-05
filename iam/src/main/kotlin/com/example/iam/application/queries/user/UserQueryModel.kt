package com.example.iam.application.queries.user

import kotlinx.serialization.Serializable

/**
 * A read-optimized model for representing user data in responses.
 */
@Serializable
data class UserQueryModel(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isActive: Boolean
)
