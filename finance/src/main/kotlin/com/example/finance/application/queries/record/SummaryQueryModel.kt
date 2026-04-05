package com.example.finance.application.queries.record

import kotlinx.serialization.Serializable

/**
 * A read-optimized model for representing financial summary data.
 * All amounts are in Indian Paisa.
 */
@Serializable
data class SummaryQueryModel(
    val totalIncome: Long,
    val totalExpenses: Long,
    val netBalance: Long,
    val incomeCount: Long,
    val expenseCount: Long
)

