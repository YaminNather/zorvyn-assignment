package com.example.iam.application.commands.setupadmin.exceptions

import com.example.iam.application.IamApplicationException

/**
 * Thrown when attempting to setup an admin user but users already exist.
 */
internal class AdminAlreadyExistsException : IamApplicationException("Users already exist.")