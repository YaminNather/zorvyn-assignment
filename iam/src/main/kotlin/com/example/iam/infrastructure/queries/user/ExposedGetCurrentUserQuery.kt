package com.example.iam.infrastructure.queries.user

import com.example.iam.application.exceptions.UserNotFoundException
import com.example.iam.application.queries.user.GetCurrentUserQuery
import com.example.iam.application.queries.user.UserQueryModel
import com.example.iam.domain.user.UserStatus
import com.example.iam.infrastructure.persistence.UsersTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.util.UUID

/**
 * Implementation of [GetCurrentUserQuery] using Exposed's R2DBC support for direct data access.
 */
internal class ExposedGetCurrentUserQuery : GetCurrentUserQuery {
    override suspend fun execute(userId: UUID): UserQueryModel = suspendTransaction {
        val row = UsersTable
            .selectAll()
            .where { UsersTable.id eq userId }
            .singleOrNull() ?: throw UserNotFoundException(userId)

        UserQueryModel(
            id = row[UsersTable.id].toString(),
            name = row[UsersTable.name],
            email = row[UsersTable.email],
            role = row[UsersTable.role],
            isActive = row[UsersTable.status] == UserStatus.ACTIVE.name
        )
    }
}
