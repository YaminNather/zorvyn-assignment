package com.example.finance.application.queries.record

import java.util.UUID

/**
 * Interface for retrieving a specific financial record by its unique identifier.
 * Decoupled from the domain repository to support read-optimized implementations.
 */
interface GetRecordQuery {
    /**
     * Executes the query and returns the requested record as a query model.
     * @param recordId The unique identifier of the record to retrieve.
     * @return The read-only record query model.
     */
    suspend fun execute(recordId: UUID): RecordQueryModel
}
