package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    val sessionId: String
)