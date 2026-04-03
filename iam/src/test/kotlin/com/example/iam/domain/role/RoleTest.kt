package com.example.iam.domain.role

import com.example.iam.domain.role.exceptions.InvalidRoleNameException
import java.util.UUID
import kotlin.test.*

class RoleTest {

    @Test
    fun `Admin should have all permissions`() {
        val role = Role.Admin
        assertTrue(role.hasPermission(Permission.DASHBOARD_VIEW))
        assertTrue(role.hasPermission(Permission.RECORDS_VIEW))
        assertTrue(role.hasPermission(Permission.RECORDS_MANAGE))
        assertTrue(role.hasPermission(Permission.USERS_MANAGE))
    }

    @Test
    fun `Analyst should have view but not manage permissions`() {
        val role = Role.Analyst
        assertTrue(role.hasPermission(Permission.DASHBOARD_VIEW))
        assertTrue(role.hasPermission(Permission.RECORDS_VIEW))
        assertFalse(role.hasPermission(Permission.RECORDS_MANAGE))
        assertFalse(role.hasPermission(Permission.USERS_MANAGE))
    }

    @Test
    fun `Viewer should only have dashboard permission`() {
        val role = Role.Viewer
        assertTrue(role.hasPermission(Permission.DASHBOARD_VIEW))
        assertFalse(role.hasPermission(Permission.RECORDS_VIEW))
    }

    @Test
    fun `should resolve role from valid name (case insensitive)`() {
        assertEquals(Role.Admin, Role.fromName("ADMIN"))
        assertEquals(Role.Analyst, Role.fromName("analyst"))
        assertEquals(Role.Viewer, Role.fromName("Viewer"))
    }

    @Test
    fun `should fail to resolve role from invalid name`() {
        assertFailsWith<InvalidRoleNameException> {
            Role.fromName("UNKNOWN")
        }
    }
}
