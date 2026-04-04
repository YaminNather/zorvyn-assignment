package com.example.iam.application.commands

import com.example.iam.application.exceptions.UserNotFoundException
import com.example.iam.domain.user.UserRepository
import java.util.UUID

/**
 * Command to orchestrate changing a user's name.
 */
internal class ChangeUserNameCommand(
    private val userRepository: UserRepository
) {
    /**
     * Executes the name change operation.
     * @param userId The ID of the user whose name should be changed.
     * @param newName The string value of the new name.
     * @throws UserNotFoundException if the user doesn't exist.
     */
    suspend fun execute(userId: UUID, newName: String) {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException(userId)
        user.changeName(newName)
        userRepository.save(user)
    }
}
