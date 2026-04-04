package com.example.iam.infrastructure.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.iam.domain.auth.JwtProvider
import java.util.Date

/**
 * Implementation of [JwtProvider] using Ktor's JWT library.
 */
internal class JwtTokenProvider(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
    private val expirationMs: Long = 3600_000 // 1 hour default
) : JwtProvider {

    override fun createToken(subject: String, roles: List<String>, permissions: List<String>): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withSubject(subject)
            .withClaim("roles", roles)
            .withClaim("permissions", permissions)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
            .sign(Algorithm.HMAC256(secret))
    }
}
