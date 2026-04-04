package com.example.iam.domain.user.exceptions

import com.example.iam.domain.exceptions.IamDomainException

/**
 * Exception thrown when an inactive user tries to authenticate.
 */
internal class UserInactiveException(val username: String) : 
    IamDomainException("User '$username' is currently inactive and cannot login.")
