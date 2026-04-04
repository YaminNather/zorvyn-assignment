package com.example.iam.domain.user

import java.util.UUID

/**
 * Repository interface for managing User domain entity persistence.
 * Since the user intents for the system to use Exposed's R2DBC support, its methods are marked as suspend.
 */
internal interface UserRepository {
    /**
     * Resolves a user by its unique identifier.
     */
    suspend fun findById(id: UUID): User?

    /**
     * Resolves a user by its email.
     */
    suspend fun findByEmail(email: String): User?

    /**
     * Persists or updates a user entity.
     */
    suspend fun save(user: User)

    /**
     * Removes a user by its unique identifier.
     */
    suspend fun delete(id: UUID)

    /**
     * Returns the total number of users.
     */
    suspend fun count(): Long

    /**
     * Returns the total number of users with a specific role name.
     */
    suspend fun countByRole(roleName: String): Long
}
