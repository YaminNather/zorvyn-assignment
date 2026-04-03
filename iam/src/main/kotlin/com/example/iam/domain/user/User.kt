package com.example.iam.domain.user

import com.example.iam.domain.role.Permission
import com.example.iam.domain.role.Role
import com.example.iam.domain.user.exceptions.InvalidPasswordHashException
import com.example.iam.domain.user.exceptions.InvalidUsernameException
import java.util.UUID

internal class User(
    val id: UUID,
    val username: String,
    val passwordHash: String,
    private var role: Role,
    private var status: UserStatus
) {
    init {
        if (username.isBlank()) throw InvalidUsernameException(username)
        if (passwordHash.isBlank()) throw InvalidPasswordHashException()
    }

    /**
     * Gets the current role of the user.
     */
    fun getRole(): Role = role

    /**
     * Gets the current status of the user.
     */
    fun getStatus(): UserStatus = status

    /**
     * Sets the user status to ACTIVE.
     */
    fun activate() {
        status = UserStatus.ACTIVE
    }

    /**
     * Sets the user status to INACTIVE.
     */
    fun deactivate() {
        status = UserStatus.INACTIVE
    }

    /**
     * Checks if the user is currently active.
     */
    fun isActive(): Boolean = status == UserStatus.ACTIVE

    /**
     * Changes the user's assigned role.
     */
    fun changeRole(newRole: Role) {
        this.role = newRole
    }

    /**
     * Checks if the user has permission to perform a specific action.
     * Permission requires the user to be active and the role to include the action.
     */
    fun canPerform(permission: Permission): Boolean {
        return isActive() && role.hasPermission(permission)
    }

    /**
     * Retrieves all allowed permissions for this user's current role.
     */
    fun getAllowedPermissions(): Set<Permission> = role.permissions

    companion object {
        /**
         * Factory method to create a NEW User.
         * Auto-generates a UUID, sets the status to ACTIVE by default, and performs validation.
         */
        fun create(username: String, passwordHash: String, role: Role): User {
            if (username.isBlank()) throw InvalidUsernameException(username)
            if (passwordHash.isBlank()) throw InvalidPasswordHashException()
            return User(
                id = UUID.randomUUID(),
                username = username,
                passwordHash = passwordHash,
                role = role,
                status = UserStatus.ACTIVE
            )
        }
    }
}
