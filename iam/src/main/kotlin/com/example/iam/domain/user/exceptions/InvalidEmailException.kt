package com.example.iam.domain.user.exceptions

import com.example.iam.domain.exceptions.IamDomainException

/**
 * Exception thrown when a user's email is invalid (e.g., blank or wrong format).
 */
internal class InvalidEmailException(val email: String) : 
    IamDomainException("Email '$email' is invalid (blank or malformed).")
