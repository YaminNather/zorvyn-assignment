package com.example.iam.infrastructure.persistence

import com.example.iam.domain.role.Role
import com.example.iam.domain.user.User
import com.example.iam.domain.user.UserRepository
import com.example.iam.domain.user.UserStatus
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert
import java.util.UUID

/**
 * Implementation of [UserRepository] using Exposed with R2DBC support.
 */
internal class ExposedUserRepository : UserRepository {
    override suspend fun findById(id: UUID): User? = suspendTransaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun findByEmail(email: String): User? = suspendTransaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun save(user: User): Unit = suspendTransaction {
        UsersTable.upsert {
            it[UsersTable.id] = user.id
            it[UsersTable.name] = user.name
            it[UsersTable.email] = user.email
            it[UsersTable.passwordHash] = user.passwordHash
            it[UsersTable.role] = user.getRole().name
            it[UsersTable.status] = user.getStatus().name
        }
    }

    override suspend fun delete(id: UUID): Unit = suspendTransaction {
        UsersTable.deleteWhere { UsersTable.id eq id }
    }

    override suspend fun count(): Long = suspendTransaction {
        UsersTable.selectAll().count()
    }

    /**
     * Reconstructs a [User] domain entity from an Exposed [ResultRow].
     */
    private fun ResultRow.toDomain(): User {
        return User(
            id = this[UsersTable.id],
            name = this[UsersTable.name],
            email = this[UsersTable.email],
            passwordHash = this[UsersTable.passwordHash],
            role = Role.fromName(this[UsersTable.role]),
            status = UserStatus.valueOf(this[UsersTable.status])
        )
    }
}
