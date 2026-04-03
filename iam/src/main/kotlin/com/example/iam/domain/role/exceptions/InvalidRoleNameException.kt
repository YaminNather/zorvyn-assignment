package com.example.iam.domain.role.exceptions

import com.example.iam.domain.exceptions.IamDomainException

/**
 * Exception thrown when a role's name is invalid (e.g., blank).
 */
internal class InvalidRoleNameException(val name: String) : 
    IamDomainException("Role name '$name' is invalid (must not be blank).")
