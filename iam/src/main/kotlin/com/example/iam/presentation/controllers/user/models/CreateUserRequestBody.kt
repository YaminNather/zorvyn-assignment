package com.example.iam.presentation.controllers.user.models

import kotlinx.serialization.Serializable

/**
 * Request body for creating a new user through the IAM API.
 */
@Serializable
internal data class CreateUserRequestBody(
    val username: String,
    val email: String,
    val password: String, // Plain-text password to be processed/hashed
    val roleName: String
)

/**
 * Response body containing the identifier of the newly created user.
 */
@Serializable
internal data class CreateUserResponseBody(
    val id: String
)
