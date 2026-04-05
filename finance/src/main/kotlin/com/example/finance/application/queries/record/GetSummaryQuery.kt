package com.example.finance.application.queries.record

import kotlin.time.Instant

/**
 * Interface for retrieving summarized financial data with support for filtering.
 */
interface GetSummaryQuery {
    /**
     * Executes the query and returns the summary matching the criteria.
     */
    suspend fun execute(
        minAmount: Long? = null,
        maxAmount: Long? = null,
        categories: List<String>? = null,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): SummaryQueryModel
}
