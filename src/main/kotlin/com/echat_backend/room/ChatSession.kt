package com.echat_backend.room

import io.ktor.websocket.*

data class ChatSession(
    val username: String,
    val session: DefaultWebSocketSession
)