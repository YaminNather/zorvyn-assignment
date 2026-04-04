package com.example.sharedkernel.security

/**
 * Interface for JWT generation and verification services.
 */
interface JwtProvider {
    /**
     * Generates a stateless authentication token.
     * @param subject The unique identifier (e.g., user ID) for the token.
     * @param roles String representation of assigned roles.
     * @param permissions Permission strings to be embedded for RBAC.
     * @return An encoded JWT string.
     */
    fun createToken(subject: String, roles: List<String>, permissions: List<String>): String
}
