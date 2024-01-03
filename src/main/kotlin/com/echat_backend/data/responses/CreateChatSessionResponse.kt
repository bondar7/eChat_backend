package com.echat_backend.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatSessionResponse(
    val sessionId: String
)