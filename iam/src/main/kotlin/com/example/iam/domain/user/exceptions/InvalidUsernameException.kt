package com.example.iam.domain.user.exceptions

import com.example.iam.domain.exceptions.IamDomainException

/**
 * Exception thrown when a user's username is invalid (e.g., blank).
 */
internal class InvalidUsernameException(val username: String) : 
    IamDomainException("Username '$username' is invalid (must not be blank).")
