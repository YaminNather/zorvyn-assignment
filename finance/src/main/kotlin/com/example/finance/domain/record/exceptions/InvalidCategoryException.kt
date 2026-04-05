package com.example.finance.domain.record.exceptions

import com.example.finance.domain.exceptions.FinanceDomainException

/**
 * Thrown when a record is created or updated with an invalid category (e.g., blank).
 */
internal class InvalidCategoryException(val category: String) : 
    FinanceDomainException("Category '$category' is invalid (must not be blank).")
