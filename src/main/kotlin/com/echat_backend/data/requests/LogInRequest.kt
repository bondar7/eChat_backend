package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class LogInRequest(
    val username: String,
    val password: String
)
