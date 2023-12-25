package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class CheckPasswordRequest(
    val usernameToFindUser: String,
    val password: String
)
