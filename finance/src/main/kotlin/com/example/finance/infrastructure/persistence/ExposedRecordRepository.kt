package com.example.finance.infrastructure.persistence

import com.example.finance.domain.record.Record
import com.example.finance.domain.record.RecordRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.upsert
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.time.Instant
import java.util.UUID

/**
 * Implementation of [RecordRepository] using Exposed with R2DBC support.
 */
internal class ExposedRecordRepository : RecordRepository {
    override suspend fun findById(id: UUID): Record? = suspendTransaction {
        RecordsTable.selectAll()
            .where { RecordsTable.id eq id }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun findByUserId(userId: UUID): List<Record> = suspendTransaction {
        RecordsTable.selectAll()
            .where { RecordsTable.userId eq userId }
            .toList()
            .map { it.toDomain() }
    }

    override suspend fun save(record: Record): Unit = suspendTransaction {
        RecordsTable.upsert {
            it[RecordsTable.id] = record.id
            it[RecordsTable.userId] = record.userId
            it[RecordsTable.amount] = record.amount
            it[RecordsTable.category] = record.category
            it[RecordsTable.dateMillis] = record.date.toEpochMilli()
            it[RecordsTable.description] = record.description
        }
    }

    override suspend fun delete(id: UUID): Unit = suspendTransaction {
        RecordsTable.deleteWhere { RecordsTable.id eq id }
    }

    override suspend fun count(): Long = suspendTransaction {
        RecordsTable.selectAll().count()
    }

    override suspend fun sumAmountByUserId(userId: UUID): Long = suspendTransaction {
        RecordsTable.selectAll()
            .where { RecordsTable.userId eq userId }
            .toList()
            .sumOf { it[RecordsTable.amount] }
    }

    private fun ResultRow.toDomain(): Record {
        return Record(
            id = this[RecordsTable.id],
            userId = this[RecordsTable.userId],
            amount = this[RecordsTable.amount],
            category = this[RecordsTable.category],
            date = Instant.ofEpochMilli(this[RecordsTable.dateMillis]),
            description = this[RecordsTable.description]
        )
    }
}
