package com.example.iam.domain.user.exceptions

import com.example.iam.domain.exceptions.IamDomainException

/**
 * Exception thrown when a user's password hash is invalid (e.g., blank).
 */
internal class InvalidPasswordHashException : 
    IamDomainException("Password hash must not be blank.")
