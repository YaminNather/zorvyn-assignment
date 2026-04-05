package com.example.finance.domain.record

import java.util.UUID

/**
 * Repository interface for managing Record domain entity persistence.
 * Since the system uses Exposed's R2DBC support, its methods are suspendable.
 */
internal interface RecordRepository {
    /**
     * Resolves a record by its unique identifier.
     */
    suspend fun findById(id: UUID): Record?

    /**
     * Resolves all records belonging to a specific user.
     */
    suspend fun findByUserId(userId: UUID): List<Record>

    /**
     * Persists or updates a record entity.
     */
    suspend fun save(record: Record)

    /**
     * Removes a record by its unique identifier.
     */
    suspend fun delete(id: UUID)

    /**
     * Returns the total number of records.
     */
    suspend fun count(): Long

    /**
     * Aggregates the total amount for a specific user. 
     * Positive amounts are income, negative are expenses.
     */
    suspend fun sumAmountByUserId(userId: UUID): Long
}
