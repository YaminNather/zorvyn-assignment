package com.example.finance.infrastructure.persistence

import com.example.finance.domain.record.Record
import com.example.finance.domain.record.RecordRepository
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.upsert
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.time.Instant
import java.util.UUID
import kotlin.time.Clock

/**
 * Implementation of [RecordRepository] using Exposed with R2DBC support.
 */
internal class ExposedRecordRepository : RecordRepository {
    override suspend fun findById(id: UUID): Record? = suspendTransaction {
        RecordsTable.selectAll()
            .where { (RecordsTable.id eq id) and (RecordsTable.deletedAt.isNull()) }
            .singleOrNull()
            ?.toDomain()

    }

    override suspend fun save(record: Record): Unit = suspendTransaction {
        val existing = RecordsTable.selectAll().where { RecordsTable.id eq record.id }.singleOrNull()

        RecordsTable.upsert {
            it[RecordsTable.id] = record.id
            it[RecordsTable.amount] = record.amount
            it[RecordsTable.category] = record.category
            it[RecordsTable.dateMillis] = record.date.toLocalDateTime(TimeZone.UTC)
            it[RecordsTable.description] = record.description
            it[RecordsTable.updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            it[RecordsTable.deletedAt] = record.deletedAt?.toLocalDateTime(TimeZone.UTC)
        }

    }

    override suspend fun delete(id: UUID): Unit = suspendTransaction {
        val record = findById(id) ?: return@suspendTransaction
        record.markAsDeleted(Clock.System.now())
        save(record)
    }


    override suspend fun count(): Long = suspendTransaction {
        RecordsTable.selectAll().where { RecordsTable.deletedAt.isNull() }.count()
    }


    private fun ResultRow.toDomain(): Record {
        val record = Record(
            id = this[RecordsTable.id],
            amount = this[RecordsTable.amount],
            category = this[RecordsTable.category],
            date = this[RecordsTable.dateMillis].toInstant(UtcOffset.ZERO),
            description = this[RecordsTable.description]
        )
        
        this[RecordsTable.deletedAt]?.let {
            record.markAsDeleted(it.toInstant(UtcOffset.ZERO))
        }
        
        return record
    }

}
