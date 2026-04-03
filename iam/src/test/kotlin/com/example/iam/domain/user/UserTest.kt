package com.example.iam.domain.user

import com.example.iam.domain.role.Permission
import com.example.iam.domain.role.Role
import com.example.iam.domain.user.exceptions.InvalidPasswordHashException
import com.example.iam.domain.user.exceptions.InvalidUsernameException
import java.util.UUID
import kotlin.test.*

class UserTest {

    private val testRole = Role.Analyst

    @Test
    fun `should create user with backend generated id and default active status`() {
        val user = User.create(username = "jdoe", passwordHash = "hashed", role = testRole)
        assertNotNull(user.id)
        assertTrue(user.isActive())
    }

    @Test
    fun `should fail to create user with blank username or password hash`() {
        assertFailsWith<InvalidUsernameException> {
            User.create(username = "", passwordHash = "hashed", role = testRole)
        }
        assertFailsWith<InvalidPasswordHashException> {
            User.create(username = "jdoe", passwordHash = " ", role = testRole)
        }
    }

    @Test
    fun `should toggle between active and inactive status`() {
        val user = User.create(username = "jdoe", passwordHash = "hashed", role = testRole)
        
        user.deactivate()
        assertFalse(user.isActive())
        assertEquals(UserStatus.INACTIVE, user.getStatus())

        user.activate()
        assertTrue(user.isActive())
        assertEquals(UserStatus.ACTIVE, user.getStatus())
    }

    @Test
    fun `should update user's role`() {
        val newRole = Role.Admin
        val user = User.create(username = "jdoe", passwordHash = "hashed", role = testRole)

        user.changeRole(newRole)
        assertEquals(newRole, user.getRole())
        assertTrue(user.canPerform(Permission.USERS_MANAGE))
    }

    @Test
    fun `should allow changing username with validation`() {
        val user = User.create(username = "jdoe", passwordHash = "hashed", role = testRole)
        user.changeUsername("janedoe")
        assertEquals("janedoe", user.username)

        assertFailsWith<InvalidUsernameException> {
            user.changeUsername(" ")
        }
    }

    @Test
    fun `should allow action if user is active and role has permission`() {
        val user = User.create(username = "jdoe", passwordHash = "hashed", role = testRole)
        assertTrue(user.canPerform(Permission.RECORDS_VIEW))
    }

    @Test
    fun `should deny action if user is inactive even if role has permission`() {
        val user = User.create(username = "jdoe", passwordHash = "hashed", role = testRole)
        user.deactivate()
        assertFalse(user.canPerform(Permission.RECORDS_VIEW))
    }

    @Test
    fun `should deny action if role does not have permission`() {
        val user = User.create(username = "jdoe", passwordHash = "hashed", role = testRole)
        assertFalse(user.canPerform(Permission.USERS_MANAGE))
    }

    @Test
    fun `should get all allowed permissions for user's role`() {
        val user = User.create(username = "jdoe", passwordHash = "hashed", role = testRole)
        assertEquals(testRole.permissions, user.getAllowedPermissions())
    }

    @Test
    fun `should allow manual reconstitution via constructor`() {
        val id = UUID.randomUUID()
        val user = User(
            id = id,
            username = "ext-user",
            passwordHash = "hash",
            role = testRole,
            status = UserStatus.INACTIVE
        )
        assertFalse(user.isActive())
        assertEquals(id, user.id)
    }
}
