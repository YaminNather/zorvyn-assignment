package com.example.finance.domain.exceptions

/**
 * Base class for all domain-specific exceptions in the Finance module.
 */
internal abstract class FinanceDomainException(message: String) : RuntimeException(message)
