package com.example.iam.application.exceptions

import java.util.UUID

/**
 * Thrown when attempting to perform operations on a non-existent user.
 */
internal class UserNotFoundException(id: UUID) : IamApplicationException("User with ID $id not found.")
