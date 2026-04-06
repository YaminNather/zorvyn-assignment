package com.example.iam.domain.validations

internal fun validatePassword(value: String): String? {
    if (value.length < 3) {
        return "should be at least 3 characters long"
    }

    return null
}