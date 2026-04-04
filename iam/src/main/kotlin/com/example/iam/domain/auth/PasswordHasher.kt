package com.example.iam.domain.auth

/**
 * Interface for password hashing and verification services.
 */
interface PasswordHasher {
    /**
     * Hashes a plain-text password.
     */
    fun hash(password: String): String

    /**
     * Verifies a plain-text password against a known hash.
     */
    fun verify(password: String, hash: String): Boolean
}
