package com.echat_backend.chat_sessions

import io.ktor.websocket.*

class Connection(val userId: String, val session: DefaultWebSocketSession) {
    suspend fun send(message: String) {
        session.send(Frame.Text(message))
    }
}