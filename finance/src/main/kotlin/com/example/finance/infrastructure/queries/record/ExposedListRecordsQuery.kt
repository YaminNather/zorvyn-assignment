package com.example.finance.infrastructure.queries.record

import com.example.finance.application.queries.record.ListRecordsQuery
import com.example.finance.application.queries.record.ListRecordsResponse
import com.example.finance.application.queries.record.RecordQueryModel
import com.example.finance.infrastructure.persistence.RecordsTable
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.time.Instant as JavaInstant


/**
 * Implementation of [ListRecordsQuery] using Exposed with R2DBC support.
 */
internal class ExposedListRecordsQuery : ListRecordsQuery {
    override suspend fun execute(
        minAmount: Long?,
        maxAmount: Long?,
        categories: List<String>?,
        startDate: kotlin.time.Instant?,
        endDate: kotlin.time.Instant?,


        page: Int,
        pageSize: Int
    ): ListRecordsResponse = suspendTransaction {
        // Build base query
        val baseQuery = RecordsTable.selectAll()
        
        // Dynamic filtering
        if (minAmount != null) {
            baseQuery.where { RecordsTable.amount greaterEq minAmount }
        }
        if (maxAmount != null) {
            baseQuery.where { RecordsTable.amount lessEq maxAmount }
        }
        if (!categories.isNullOrEmpty()) {
            baseQuery.where { RecordsTable.category inList categories }
        }
        if (startDate != null) {
            baseQuery.where { RecordsTable.dateMillis greaterEq startDate.toEpochMilliseconds() }
        }
        if (endDate != null) {
            baseQuery.where { RecordsTable.dateMillis lessEq endDate.toEpochMilliseconds() }
        }


        
        // Count total matches (before pagination)
        val totalCount = baseQuery.count()
        
        // Map result rows to query models with pagination and sorting
        val items = baseQuery
            .orderBy(RecordsTable.dateMillis, SortOrder.DESC) // Most recent first
            .limit(pageSize)
            .offset(((page - 1).coerceAtLeast(0) * pageSize).toLong())
            .toList()
            .map { 
                RecordQueryModel(
                    id = it[RecordsTable.id].toString(),
                    amount = it[RecordsTable.amount],
                    category = it[RecordsTable.category],
                    date = kotlin.time.Instant.fromEpochMilliseconds(it[RecordsTable.dateMillis]),


                    description = it[RecordsTable.description]
                )
            }
            
        ListRecordsResponse(
            items = items,
            totalCount = totalCount,
            page = page,
            pageSize = pageSize
        )
    }
}
