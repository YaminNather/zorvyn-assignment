package com.example.iam.application.exceptions

/**
 * Thrown when an invalid role name is requested during an application operation.
 */
internal class InvalidRoleException(roleName: String) : IamApplicationException("Role '$roleName' is invalid or does not exist.")
