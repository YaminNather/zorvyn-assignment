package com.example.iam.domain.user.exceptions

import com.example.iam.domain.exceptions.IamDomainException

/**
 * Thrown when a provided name is empty or invalid.
 */
internal class InvalidNameException(val name: String) : IamDomainException("Name cannot be blank.")
