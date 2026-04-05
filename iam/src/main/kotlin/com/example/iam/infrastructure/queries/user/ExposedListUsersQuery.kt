package com.example.iam.infrastructure.queries.user

import com.example.iam.application.queries.user.ListUsersQuery
import com.example.iam.application.queries.user.UserQueryModel
import com.example.iam.domain.user.UserStatus
import com.example.iam.infrastructure.persistence.UsersTable
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

/**
 * Implementation of [ListUsersQuery] using Exposed's R2DBC support.
 */
internal class ExposedListUsersQuery : ListUsersQuery {
    override suspend fun execute(): List<UserQueryModel> = suspendTransaction {
        UsersTable
            .selectAll()
            .toList()
            .map { row ->
                UserQueryModel(
                    id = row[UsersTable.id].toString(),
                    name = row[UsersTable.name],
                    email = row[UsersTable.email],
                    role = row[UsersTable.role],
                    isActive = row[UsersTable.status] == UserStatus.ACTIVE.name
                )
            }
    }
}
