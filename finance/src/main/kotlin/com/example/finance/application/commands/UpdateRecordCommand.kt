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
        userId: UUID,
        recordId: UUID,
        amount: Long?,
        category: String?,
        date: Instant?,
        description: String?
    ) {
        // Fetch existing record
        val record = recordRepository.findById(recordId)
            ?: throw RecordNotFoundException(recordId)

        // Ownership Check: Users can only modify their own financial records
        if (record.userId != userId) {
            throw UnauthorizedRecordAccessException(userId, recordId)
        }

        // Apply selectively provided updates via domain methods
        amount?.let { record.changeAmount(it) }
        category?.let { record.changeCategory(it) }
        date?.let { record.changeDate(it) }
        
        // Pass the description directly as it can be null (intentional reset)
        // Note: For now, we assume if it's passed here, it's intended to be updated.
        // If we want a skip-only-if-null pattern, we'd need a different approach.
        // For this assignment, we'll keep it simple.
        if (description != null) {
            record.changeDescription(description)
        }

        // Persist changes
        recordRepository.save(record)
    }
}
