package com.example.iam.application.exceptions

import java.lang.RuntimeException

/**
 * Exception thrown when attempting to create a user with an email that already exists in the system.
 */
class UserAlreadyExistsException(val email: String) : RuntimeException("User with email '$email' already exists.")
