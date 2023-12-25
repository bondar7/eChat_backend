package com.echat_backend.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val username: String,
    val email: String,
    val bio: String = "Bio",
    val token: String
)
