package com.echat_backend.security.hashing

data class SaltedHash (
    val hash: String,
    val salt: String
)