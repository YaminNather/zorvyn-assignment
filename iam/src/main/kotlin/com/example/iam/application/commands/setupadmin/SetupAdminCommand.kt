package com.example.iam.application.commands.setupadmin

import com.example.iam.application.commands.setupadmin.exceptions.AdminAlreadyExistsException
import com.example.iam.domain.auth.PasswordHasher
import com.example.iam.domain.role.Role
import com.example.iam.domain.user.User
import com.example.iam.domain.user.UserRepository
import java.util.UUID

/**
 * Use case command to initialize the first admin user in the system.
 */
internal class SetupAdminCommand(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
) {
    /**
     * Executes the setup admin process.
     * @return The unique identifier of the newly created admin.
     * @throws com.example.iam.application.commands.setupadmin.exceptions.AdminAlreadyExistsException if there are already users in the system.
     */
    suspend fun execute(): UUID {
        if (userRepository.count() > 0) {
            throw AdminAlreadyExistsException()
        }

        val user = User.create(
            name = "admin",
            email = "admin@example.com",
            passwordHash = passwordHasher.hash("admin"),
            role = Role.Admin
        )

        userRepository.save(user)

        return user.id
    }
}
