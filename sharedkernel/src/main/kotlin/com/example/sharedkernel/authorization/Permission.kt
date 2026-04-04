package com.example.sharedkernel.authorization

/**
 * Enumeration of all permissions supported by the system.
 * Centralized in sharedkernel to allow for typed permission-based guards.
 */
enum class Permission(val value: String) {
    DASHBOARD_VIEW("dashboard:view"),
    RECORDS_VIEW("records:view"),
    RECORDS_MANAGE("records:manage"),
    USERS_MANAGE("users:manage")
}
