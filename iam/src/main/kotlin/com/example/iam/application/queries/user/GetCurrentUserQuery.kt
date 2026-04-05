package com.example.iam.application.queries.user

import java.util.UUID

/**
 * Interface for retrieving the currently authenticated user's details.
 */
interface GetCurrentUserQuery {
    /**
     * Executes the query and returns the user's details as a query model.
     * @param userId The unique identifier of the authenticated user.
     * @return The read-only user query model.
     */
    suspend fun execute(userId: UUID): UserQueryModel
}
