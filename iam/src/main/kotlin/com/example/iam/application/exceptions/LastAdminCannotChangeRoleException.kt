package com.example.iam.application.exceptions

/**
 * Thrown when attempting to change the role of the last admin.
 */
internal class LastAdminCannotChangeRoleException : IamApplicationException("Cannot change the role of the last remaining admin.")
