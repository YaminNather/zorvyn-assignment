package com.example.sharedkernel.authorization

import com.example.sharedkernel.errorhandling.ProblemJsonException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * A Ktor route-scoped plugin that validates JWT permissions.
 * It expects a [JWTPrincipal] to be present after successful authentication.
 */
private val PermissionValidatorPlugin = createRouteScopedPlugin(
    "PermissionValidator",
    ::PermissionValidatorConfig
) {
    val requiredPermission = pluginConfig.requiredPermission

    // Intercept after authentication to verify permissions from the JWT payload
    on(AuthenticationChecked) { call ->
        val principal = call.principal<JWTPrincipal>()
        
        // Extract permissions from the 'permissions' claim in the token
        val permissions = principal?.payload?.getClaim("permissions")?.asList(String::class.java)

        // Deny access if permissions claim is missing or doesn't contain the required permission
        if (permissions == null || requiredPermission.value !in permissions) {
            val exception = ProblemJsonException(
                type = "forbidden",
                title = "Access Forbidden",
                detail = "Missing required permission: ${requiredPermission.value}",
                statusCode = HttpStatusCode.Forbidden.value
            )

            call.response.headers.append(HttpHeaders.ContentType, "application/problem+json")
            call.response.status(HttpStatusCode.fromValue(exception.statusCode))
            call.respond(exception)

            return@on
        }
    }
}

/**
 * Configuration class for the [PermissionValidatorPlugin].
 */
private class PermissionValidatorConfig {
    lateinit var requiredPermission: Permission
}

/**
 * DSL extension to wrap a route with permission-based authorization.
 * 
 * Usage:
 * ```kotlin
 * authenticate("auth-jwt") {
 *     withPermission(Permission.RECORDS_VIEW) {
 *         get("/records") { ... }
 *     }
 * }
 * ```
 */
fun Route.withPermission(permission: Permission, build: Route.() -> Unit): Route {
    val authorizedRoute = createChild(PermissionRouteSelector(permission))
    authorizedRoute.install(PermissionValidatorPlugin) {
        this.requiredPermission = permission
    }
    authorizedRoute.build()
    return authorizedRoute
}

/**
 * Selector for identifying permission-guarded routes in the routing tree.
 */
private class PermissionRouteSelector(val permission: Permission) : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Constant
    }

    override fun toString(): String = "(permission: ${permission.value})"
}