package com.example.iam.application.commands

import com.example.iam.application.exceptions.LastAdminCannotChangeRoleException
import com.example.iam.application.exceptions.UserNotFoundException
import com.example.iam.domain.role.Role
import com.example.iam.domain.user.UserRepository
import java.util.UUID

/**
 * Command to orchestrate changing a user's role.
 */
internal class ChangeUserRoleCommand(
    private val userRepository: UserRepository
) {
    /**
     * Executes the role change operation.
     * @param userId The ID of the user whose role should be changed.
     * @param newRoleName The string name of the new role.
     * @throws UserNotFoundException if the user doesn't exist.
     * @throws LastAdminCannotChangeRoleException if the operation attempts to remove the last admin's role.
     */
    suspend fun execute(userId: UUID, newRoleName: String) {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException(userId)
        val newRole = try {
            Role.fromName(newRoleName)
        } catch (e: com.example.iam.domain.role.exceptions.InvalidRoleNameException) {
            throw com.example.iam.application.exceptions.InvalidRoleException(newRoleName)
        }

        // Protect the last admin from losing their permissions.
        if (user.getRole() == Role.Admin && newRole != Role.Admin) {
            val adminCount = userRepository.countByRole(Role.Admin.name)
            if (adminCount <= 1L) {
                throw LastAdminCannotChangeRoleException()
            }
        }

        user.changeRole(newRole)
        userRepository.save(user)
    }
}
