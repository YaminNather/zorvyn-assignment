package com.example.iam.infrastructure.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.iam.domain.auth.PasswordHasher

/**
 * Implementation of [PasswordHasher] using the BCrypt algorithm.
 */
internal class BcryptPasswordHasher : PasswordHasher {
    override fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    override fun verify(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }
}
