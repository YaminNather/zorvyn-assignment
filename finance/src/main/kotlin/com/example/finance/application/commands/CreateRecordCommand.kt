package com.example.finance.application.commands

import com.example.finance.domain.record.Record
import com.example.finance.domain.record.RecordRepository
import java.time.Instant
import java.util.UUID

/**
 * Command to create a new financial record.
 * Orchestrates domain entity construction and persistence.
 */
internal class CreateRecordCommand(
    private val recordRepository: RecordRepository
) {
    /**
     * Executes the record creation process.
     * @return The unique identifier of the newly created record.
     */
    suspend fun execute(
        userId: UUID,
        amount: Long,
        category: String,
        date: Instant,
        description: String? = null
    ): UUID {
        // Create the domain entity (validations happen inside Record.create and Record init)
        val record = Record.create(
            userId = userId,
            amount = amount,
            category = category,
            date = date,
            description = description
        )

        // Persist the record
        recordRepository.save(record)

        return record.id
    }
}
