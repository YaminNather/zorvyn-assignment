package com.example.iam.application.commands

import com.example.iam.domain.user.UserRepository
import com.example.iam.domain.user.exceptions.AuthenticationException
import com.example.iam.domain.user.exceptions.UserInactiveException
import com.example.sharedkernel.security.JwtProvider
import com.example.sharedkernel.security.PasswordHasher

/**
 * Command to orchestrate user authentication and stateless token issuance.
 * Ensures username/password validity and check's the user's current activity status.
 */
internal class LoginCommand(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtProvider: JwtProvider
) {
    /**
     * Authenticates a user and generates an access token.
     * @param username The provided username for authentication.
     * @param password The provided password for verification.
     * @return An encoded access token (JWT).
     */
    suspend fun execute(username: String, password: String): String {
        // Find user by username
        val user = userRepository.findByUsername(username) ?: throw AuthenticationException()

        // Validate user's current status before allowing login
        if (!user.isActive()) throw UserInactiveException(username)

        // Verify provided password against stored hash
        if (!passwordHasher.verify(password, user.passwordHash)) throw AuthenticationException()

        // Generate stateless token with subject (userId), roles, and permissions
        return jwtProvider.createToken(
            subject = user.id.toString(),
            roles = listOf(user.getRole().name),
            permissions = user.getAllowedPermissions().map { it.value }
        )
    }
}
