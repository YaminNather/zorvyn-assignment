package com.example.finance.application.queries.record

import kotlin.time.Instant
import kotlinx.serialization.Serializable

/**
 * Interface for listing financial records with support for filtering and pagination.
 */
interface ListRecordsQuery {
    /**
     * Executes the query and returns a paginated list of records matching the criteria.
     */
    suspend fun execute(
        minAmount: Long? = null,
        maxAmount: Long? = null,
        categories: List<String>? = null,
        startDate: Instant? = null,
        endDate: Instant? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): ListRecordsResponse
}

/**
 * Response model for paginated record listings.
 */
@Serializable
data class ListRecordsResponse(
    val items: List<RecordQueryModel>,
    val totalCount: Long,
    val page: Int,
    val pageSize: Int
)
