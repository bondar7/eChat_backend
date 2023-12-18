package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val phoneNumber: String,
    val password: String
)