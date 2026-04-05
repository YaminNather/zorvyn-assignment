package com.example.finance.infrastructure.queries.record

import com.example.finance.application.exceptions.RecordNotFoundException
import com.example.finance.application.queries.record.GetRecordQuery
import com.example.finance.application.queries.record.RecordQueryModel
import com.example.finance.infrastructure.persistence.RecordsTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.time.Instant
import java.util.*

/**
 * Implementation of [GetRecordQuery] using Exposed with R2DBC support.
 * Directly fetches data from the database, bypassing the domain repository layer.
 */
internal class ExposedGetRecordQuery : GetRecordQuery {
    override suspend fun execute(recordId: UUID): RecordQueryModel = suspendTransaction {
        // Direct SQL-like query to fetch required fields
        RecordsTable.selectAll()
            .where { RecordsTable.id eq recordId }
            .singleOrNull()
            ?.let { 
                RecordQueryModel(
                    id = it[RecordsTable.id].toString(),
                    amount = it[RecordsTable.amount],
                    category = it[RecordsTable.category],
                    date = Instant.ofEpochMilli(it[RecordsTable.dateMillis]).toString(),
                    description = it[RecordsTable.description]
                )
            } ?: throw RecordNotFoundException(recordId)
    }
}
