package com.example.iam.presentation.controllers.auth.models

import kotlinx.serialization.Serializable

/**
 * Request body for authentication (Login).
 */
@Serializable
internal data class LoginRequestBody(
    val username: String,
    val password: String
)

/**
 * Response body for successful authentication.
 * Returns the access token.
 */
@Serializable
internal data class LoginResponseBody(
    val accessToken: String
)
