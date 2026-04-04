package com.example.iam.domain.user.exceptions

import com.example.iam.domain.exceptions.IamDomainException

/**
 * Exception thrown when authentication fails (e.g., incorrect credentials).
 * We keep the message generic for security reasons.
 */
internal class AuthenticationException : IamDomainException("Invalid email or password.")
