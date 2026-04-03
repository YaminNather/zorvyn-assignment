package com.example.finance.presentation.controllers.transaction.models

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateTransactionResponseBody(
    val id: String,
)