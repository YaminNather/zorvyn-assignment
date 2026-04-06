package com.example.finance.infrastructure.queries.record

import com.example.finance.application.queries.record.GetSummaryQuery
import com.example.finance.application.queries.record.SummaryQueryModel
import com.example.finance.infrastructure.persistence.RecordsTable
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.datetime.LocalDateTimeColumnType
import org.jetbrains.exposed.v1.datetime.KotlinLocalDateColumnType
import org.jetbrains.exposed.v1.datetime.KotlinLocalDateTimeColumnType
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.math.abs
import kotlin.to

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
        val queryArgs = mutableListOf<Pair<IColumnType<*>, Any?>>()
        val conditions = mutableListOf("deleted_at IS NULL")

        // Build Dynamic WHERE Clause
        minAmount?.let { conditions.add("amount >= $${queryArgs.size + 1}"); queryArgs.add(LongColumnType() to it) }
        maxAmount?.let { conditions.add("amount <= $${queryArgs.size + 1}"); queryArgs.add(LongColumnType() to it) }
        if (!categories.isNullOrEmpty()) {
            val placeholders = categories.indices.joinToString(",") { "$${queryArgs.size + it + 1}" }
            conditions.add("category IN ($placeholders)")
            queryArgs.addAll(categories.map { TextColumnType() to it })
        }
        startDate?.let {
            conditions.add("date_millis >= $${queryArgs.size + 1}")
            queryArgs.add(KotlinLocalDateTimeColumnType() to it.toLocalDateTime(TimeZone.UTC))
        }
        endDate?.let {
            conditions.add("date_millis <= $${queryArgs.size + 1}")
            queryArgs.add(KotlinLocalDateTimeColumnType() to it.toLocalDateTime(TimeZone.UTC))
        }

        val whereClause = conditions.joinToString(" AND ")

        // Execute Single Aggregate Query
        val sql = """
            SELECT 
                SUM(CASE WHEN amount > 0 THEN amount ELSE 0 END) as total_income,
                COUNT(CASE WHEN amount > 0 THEN 1 END) as income_count,
                SUM(CASE WHEN amount < 0 THEN ABS(amount) ELSE 0 END) as total_expenses,
                COUNT(CASE WHEN amount < 0 THEN 1 END) as expense_count
            FROM ${RecordsTable.nameInDatabaseCase()}
            WHERE $whereClause
        """.trimIndent()

        val result = exec(sql, args = queryArgs) { row ->
            val totalIncome = row.get("total_income", Long::class.java) ?: 0L
            val incomeCount = row.get("income_count", Long::class.java) ?: 0L
            val totalExpenses = row.get("total_expenses", Long::class.java) ?: 0L
            val expenseCount = row.get("expense_count", Long::class.java) ?: 0L

            SummaryQueryModel(
                totalIncome = totalIncome,
                totalExpenses = totalExpenses,
                netBalance = totalIncome - totalExpenses,
                incomeCount = incomeCount,
                expenseCount = expenseCount
            )
        }
            ?.singleOrNull() ?: SummaryQueryModel(0, 0, 0, 0, 0)

        result
    }
}
