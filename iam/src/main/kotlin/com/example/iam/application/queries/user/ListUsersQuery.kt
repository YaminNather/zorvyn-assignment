package com.example.iam.application.queries.user

/**
 * Interface to retrieve a listing of all registered users in the system.
 */
interface ListUsersQuery {
    /**
     * Executes the query and returns a list of all user query models.
     */
    suspend fun execute(): List<UserQueryModel>
}
