package com.example.iam.application.commands

import com.example.iam.domain.user.User
import com.example.iam.domain.user.UserRepository
import com.example.iam.domain.role.Role
import com.example.sharedkernel.security.PasswordHasher
import java.util.UUID

/**
 * Command to create a new user by coordinating role resolution, 
 * password hashing, entity construction, and persistence.
 */
internal class CreateUserCommand(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
) {
    /**
     * Executes the user creation process.
     * @param password The raw initial password provided by the administrator.
     * @return The unique identifier of the newly created user.
     */
    suspend fun execute(
        username: String,
        email: String,
        password: String,
        roleName: String
    ): UUID {
        // Resolve the static role
        val role = Role.fromName(roleName)
        
        // Securely hash the password before it reaches the domain entity
        val passwordHash = passwordHasher.hash(password)
        
        // Create the domain entity
        val user = User.create(
            username = username,
            email = email,
            passwordHash = passwordHash,
            role = role
        )
        
        // Persist the user
        userRepository.save(user)
        
        return user.id
    }
}
