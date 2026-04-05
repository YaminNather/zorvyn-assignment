package com.example.iam.application.commands

import com.example.iam.domain.user.UserRepository
import com.example.iam.domain.user.exceptions.AuthenticationException
import com.example.iam.domain.user.exceptions.UserInactiveException
import com.example.iam.domain.auth.JwtProvider
import com.example.iam.domain.auth.PasswordHasher

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
     * Executes the login process.
     * @param email The user's email.
     * @param password The user's plain-text password.
     * @return A valid JWT string.
     * @throws AuthenticationException if credentials are invalid or the account is inactive.
     */
    suspend fun execute(email: String, password: String): String {
        // 1. Find user by email
        val user = userRepository.findByEmail(email)
            ?: throw AuthenticationException()

        // Validate user's current status before allowing login
        if (!user.isActive()) throw UserInactiveException(email)

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
