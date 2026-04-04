package com.example.iam.application.commands

import com.example.iam.domain.role.Role
import com.example.iam.domain.user.User
import com.example.iam.domain.user.UserRepository
import com.example.iam.domain.auth.PasswordHasher
import java.util.UUID
import kotlin.test.*
import kotlinx.coroutines.runBlocking

/**
 * Unit tests for the CreateUserCommand implementation.
 */
internal class CreateUserCommandTest {

    private class FakeUserRepository : UserRepository {
        var savedUser: User? = null
        override suspend fun findById(id: UUID): User? = null
        override suspend fun findByEmail(email: String): User? = null
        override suspend fun save(user: User) {
            savedUser = user
        }
        override suspend fun delete(id: UUID) {}
        override suspend fun count(): Long = 0
    }

    private class FakePasswordHasher : PasswordHasher {
        override fun hash(password: String): String = "hashed_pw"
        override fun verify(password: String, hash: String): Boolean = true
    }

    @Test
    fun `should correctly coordinate role resolution, hashing and user persistence`() = runBlocking {
        val repo = FakeUserRepository()
        val hasher = FakePasswordHasher()
        val command = CreateUserCommand(repo, hasher)

        val id = command.execute(
            name = "admin_user",
            email = "admin@example.com",
            password = "raw_password",
            roleName = "ADMIN"
        )

        // Ensure a valid UUID is returned
        assertNotNull(id)
        
        // Confirm the user was correctly constructed and sent to the repository
        val saved = repo.savedUser
        assertNotNull(saved)
        assertEquals("admin_user", saved.name)
        assertEquals("admin@example.com", saved.email)
        assertEquals("hashed_pw", saved.passwordHash) // Verifies hashing step
        assertEquals(Role.Admin, saved.getRole())
    }
    
    @Test
    fun `should fail if invalid role is provided`() = runBlocking {
        val repo = FakeUserRepository()
        val hasher = FakePasswordHasher()
        val command = CreateUserCommand(repo, hasher)

        assertFails {
            command.execute(
                name = "jdoe",
                email = "jdoe@example.com",
                password = "raw",
                roleName = "SUPER_USER" // This role doesn't exist
            )
        }
    }
}
