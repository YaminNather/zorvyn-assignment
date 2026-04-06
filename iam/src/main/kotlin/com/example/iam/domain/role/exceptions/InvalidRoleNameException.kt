package com.example.iam.domain.role.exceptions

import com.example.iam.domain.exceptions.IamDomainException
import com.example.iam.domain.role.Role

/**
 * Exception thrown when a role's name is invalid (e.g., blank).
 */
internal class InvalidRoleNameException(val name: String) : 
    IamDomainException("Role name '$name' is invalid, valid values are ${Role.values.joinToString()}.")
