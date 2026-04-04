package com.example.iam.presentation.controllers.user.models

import kotlinx.serialization.Serializable

@Serializable
internal data class ChangeUserNameRequestBody(
    val name: String
)
