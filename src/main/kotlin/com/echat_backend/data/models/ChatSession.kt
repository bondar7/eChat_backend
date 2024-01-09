package com.echat_backend.data.models

import com.echat_backend.data.responses.Person
import kotlinx.serialization.Serializable

@Serializable
data class ChatSession(
    val sessionId: String,
    val user: Person,
    val lastMessage: String,
    val lastMessageSentTime: Long?
)
