package com.example.iam.domain.validations

internal fun validateEmail(email: String): String? {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    if (email.matches(emailRegex.toRegex())) {
        return null
    }

    return "should be formatted like this xxxx@host.com"
}
