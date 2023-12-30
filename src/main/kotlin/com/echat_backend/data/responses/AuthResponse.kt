package com.echat_backend.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val username: String,
    val name : String? = null,
    val email: String,
    val bio: String = "Bio",
    val avatar: ByteArray? = null,
    val token: String
)
