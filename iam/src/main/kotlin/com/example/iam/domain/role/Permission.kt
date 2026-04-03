package com.example.iam.domain.role

internal enum class Permission(val value: String) {
    DASHBOARD_VIEW("dashboard:view"),
    RECORDS_VIEW("records:view"),
    RECORDS_MANAGE("records:manage"),
    USERS_MANAGE("users:manage")
}
