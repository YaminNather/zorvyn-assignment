package com.example.iam.domain.exceptions

/**
 * Base class for all domain-specific exceptions in the IAM module.
 */
internal abstract class IamDomainException(message: String) : RuntimeException(message)
