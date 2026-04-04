package com.example.iam.application.commands

import com.example.iam.domain.user.User
import com.example.iam.domain.user.UserRepository
import com.example.iam.domain.user.exceptions.AuthenticationException
import com.example.iam.domain.user.exceptions.UserInactiveException
import com.example.iam.domain.role.Role
import com.example.iam.domain.auth.JwtProvider
import com.example.iam.domain.auth.PasswordHasher
import java.util.UUID
import kotlin.test.*
import kotlinx.coroutines.runBlocking

/**
 * Unit tests for the LoginCommand orchestration.
 */
internal class LoginCommandTest {

    private class FakeUserRepository(private val user: User? = null) : UserRepository {
        private val users = if (user != null) mutableMapOf(user.id to user) else mutableMapOf()
        override suspend fun findById(id: UUID): User? = null
        override suspend fun findByEmail(email: String): User? = user
        override suspend fun save(user: User) {}
        override suspend fun delete(id: UUID) {}
        override suspend fun count(): Long = users.size.toLong()
        override suspend fun countByRole(roleName: String): Long = users.values.count { it.getRole().name == roleName }.toLong()
    }

    private class FakePasswordHasher(private val match: Boolean = true) : PasswordHasher {
        override fun hash(password: String): String = "hashed_pw"
        override fun verify(password: String, hash: String): Boolean = match
    }

    private class FakeJwtProvider(private val token: String = "valid_jwt") : JwtProvider {
        override fun createToken(subject: String, roles: List<String>, permissions: List<String>): String = token
    }

    private val activeUser = User.create("jdoe", "jdoe@example.com", "hashed_pw", Role.Admin)

    @Test
    fun `should return access token if credentials are valid and user is active`() = runBlocking {
        val command = LoginCommand(
            userRepository = FakeUserRepository(activeUser),
            passwordHasher = FakePasswordHasher(match = true),
            jwtProvider = FakeJwtProvider(token = "expected_token")
        )

        val result = command.execute("jdoe@example.com", "correct_password")
        assertEquals("expected_token", result)
    }

    @Test
    fun `should throw AuthenticationException when user does not exist`() = runBlocking {
        val command = LoginCommand(
            userRepository = FakeUserRepository(null),
            passwordHasher = FakePasswordHasher(),
            jwtProvider = FakeJwtProvider()
        )

        assertFailsWith<AuthenticationException> {
            command.execute("jdoe@example.com", "any")
        }
    }

    @Test
    fun `should throw AuthenticationException when password does not match`() = runBlocking {
        val command = LoginCommand(
            userRepository = FakeUserRepository(activeUser),
            passwordHasher = FakePasswordHasher(match = false),
            jwtProvider = FakeJwtProvider()
        )

        assertFailsWith<AuthenticationException> {
            command.execute("jdoe@example.com", "wrong_password")
        }
    }

    @Test
    fun `should throw UserInactiveException when user is not active`() = runBlocking {
        activeUser.deactivate()
        val command = LoginCommand(
            userRepository = FakeUserRepository(activeUser),
            passwordHasher = FakePasswordHasher(match = true),
            jwtProvider = FakeJwtProvider()
        )

        assertFailsWith<UserInactiveException> {
            command.execute("jdoe@example.com", "any_password")
        }
    }
}
