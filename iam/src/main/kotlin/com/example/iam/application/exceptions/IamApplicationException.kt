package com.example.iam.application.exceptions

/**
 * Base class for all application-specific exceptions in the IAM module.
 */
internal abstract class IamApplicationException(message: String) : RuntimeException(message)
