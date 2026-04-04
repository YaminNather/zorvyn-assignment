package com.example.finance.presentation.controllers.transaction.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateTransactionRequestBody(
    val amount: Int,
    val recordType: String
)
