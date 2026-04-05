package com.example.finance.application.exceptions

import java.util.UUID

/**
 * Base class for all application-layer exceptions in the finance module.
 */
internal open class FinanceApplicationException(message: String) : RuntimeException(message)

/**
 * Thrown when a resource (e.g. record) is not found.
 */
internal class RecordNotFoundException(id: UUID) : 
    FinanceApplicationException("Record with ID $id not found.")

/**
 * Thrown when a user attempts to access or modify a record they do not own.
 */
internal class UnauthorizedRecordAccessException(userId: UUID, recordId: UUID) : 
    FinanceApplicationException("User $userId does not have permission to access record $recordId.")
