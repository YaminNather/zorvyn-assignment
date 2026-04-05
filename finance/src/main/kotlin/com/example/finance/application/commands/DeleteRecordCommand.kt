package com.example.finance.application.commands

import com.example.finance.application.exceptions.RecordNotFoundException
import com.example.finance.domain.record.RecordRepository
import java.util.UUID

/**
 * Command to remove an existing financial record.
 */
internal class DeleteRecordCommand(
    private val recordRepository: RecordRepository
) {
    /**
     * Executes the record removal process.
     * @param recordId The unique identifier of the record to delete.
     */
    suspend fun execute(recordId: UUID) {
        // Verify existence before deletion
        recordRepository.findById(recordId)
            ?: throw RecordNotFoundException(recordId)

        // Perform removal
        recordRepository.delete(recordId)
    }
}
