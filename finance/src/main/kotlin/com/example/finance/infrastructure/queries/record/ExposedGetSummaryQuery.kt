package com.example.finance.infrastructure.queries.record

import com.example.finance.application.queries.record.GetSummaryQuery
import com.example.finance.application.queries.record.SummaryQueryModel
import com.example.finance.infrastructure.persistence.RecordsTable
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.math.abs

/**
 * Implementation of [GetSummaryQuery] using Exposed with R2DBC support.
 */
internal class ExposedGetSummaryQuery : GetSummaryQuery {
    override suspend fun execute(
        minAmount: Long?,
        maxAmount: Long?,
        categories: List<String>?,
        startDate: kotlin.time.Instant?,
        endDate: kotlin.time.Instant?
    ): SummaryQueryModel = suspendTransaction {
        // Build base query
        val baseQuery = RecordsTable.selectAll().where { RecordsTable.deletedAt.isNull() }


        // Apply filters (reusing logic from ListRecordsQuery)
        val filters = mutableListOf<() -> Op<Boolean>>()

        if (minAmount != null) filters += { RecordsTable.amount greaterEq minAmount }
        if (maxAmount != null) filters += { RecordsTable.amount lessEq maxAmount }
        if (!categories.isNullOrEmpty()) filters += { RecordsTable.category inList categories }
        if (startDate != null) filters += { RecordsTable.dateMillis greaterEq startDate.toLocalDateTime(TimeZone.UTC) }
        if (endDate != null) filters += { RecordsTable.dateMillis lessEq endDate.toLocalDateTime(TimeZone.UTC) }

        filters.forEachIndexed { index, e ->
            baseQuery.andWhere(e)
        }

        // Fetch filtered records - for summary we can calculate in memory to avoid multiple aggregations
        // and keep the implementation straightforward for now.
        val records = baseQuery.toList()

        var totalIncome = 0L
        var totalExpenses = 0L
        var incomeCount = 0L
        var expenseCount = 0L

        for (row in records) {
            val amount = row[RecordsTable.amount]
            if (amount > 0) {
                totalIncome += amount
                incomeCount++
            } else {
                totalExpenses += abs(amount)
                expenseCount++
            }
        }

        SummaryQueryModel(
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            netBalance = totalIncome - totalExpenses,
            incomeCount = incomeCount,
            expenseCount = expenseCount
        )

    }
}
