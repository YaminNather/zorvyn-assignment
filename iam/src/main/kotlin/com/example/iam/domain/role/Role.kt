package com.example.iam.domain.role

import com.example.iam.domain.role.exceptions.InvalidRoleNameException
import java.util.UUID

/**
 * Sealed class representing predefined system roles with static permissions.
 */
internal sealed class Role(
    val name: String,
    val permissions: Set<Permission>
) {
    /**
     * Checks if the role allows a specific permission.
     */
    fun hasPermission(permission: Permission): Boolean = permissions.contains(permission)

    /**
     * Admin role: full management access.
     */
    data object Admin : Role(
        name = "ADMIN",
        permissions = setOf(
            Permission.DASHBOARD_VIEW,
            Permission.RECORDS_VIEW,
            Permission.RECORDS_MANAGE,
            Permission.USERS_MANAGE
        )
    )

    /**
     * Analyst role: view records and summaries.
     */
    data object Analyst : Role(
        name = "ANALYST",
        permissions = setOf(
            Permission.DASHBOARD_VIEW,
            Permission.RECORDS_VIEW
        )
    )

    /**
     * Viewer role: only view general dashboard data.
     */
    data object Viewer : Role(
        name = "VIEWER",
        permissions = setOf(
            Permission.DASHBOARD_VIEW
        )
    )

    companion object {
        /**
         * List of all predefined roles in the application, initialized lazily to avoid NPE.
         */
        val values: List<Role> by lazy { listOf(Admin, Analyst, Viewer) }

        /**
         * Resolves a Role instance from its string name.
         * Useful for database reconstitution and API requests.
         */
        fun fromName(name: String): Role {
            return values.find { it.name.equals(name, ignoreCase = true) }
                ?: throw InvalidRoleNameException(name)
        }
    }
}
