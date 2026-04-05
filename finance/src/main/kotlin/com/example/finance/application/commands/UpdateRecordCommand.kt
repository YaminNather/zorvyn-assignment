package com.example.finance.application.commands

import com.example.finance.application.exceptions.RecordNotFoundException
import com.example.finance.application.exceptions.UnauthorizedRecordAccessException
import com.example.finance.domain.record.RecordRepository
import java.time.Instant
import java.util.*

/**
 * Command to update an existing financial record.
 * Handles ownership verification, entity retrieval, field updates, and persistence.
 */
internal class UpdateRecordCommand(
    private val recordRepository: RecordRepository
) {
    /**
     * Executes the record update process.
     * @param userId The ID of the authenticated user attempting the update.
     * @param recordId The unique identifier of the record to modify.
     * @param amount The new amount to set, or null to keep current value.
     * @param category The new category to set, or null to keep current value.
     * @param date The new date to set, or null to keep current value.
     * @param description The new description to set. If null is passed via DTO, it updates the record.
     */
    suspend fun execute(
        recordId: UUID,
        amount: Long?,
        category: String?,
        date: Instant?,
        description: String?
    ) {
        val record = recordRepository.findById(recordId)
            ?: throw RecordNotFoundException(recordId)

        amount?.let { record.changeAmount(it) }
        category?.let { record.changeCategory(it) }
        date?.let { record.changeDate(it) }
        
        if (description != null) {
            record.changeDescription(description)
        }

        recordRepository.save(record)
    }
}
