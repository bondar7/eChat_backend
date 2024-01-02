package com.echat_backend.chat_sessions

import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.models.Message

class SessionManager(
   private val messageDataSource: MessageDataSource
) {
    private val sessions = mutableMapOf<String, MutableList<Connection>>()

    fun registerConnection(sessionId: String, connection: Connection) {
        sessions.computeIfAbsent(sessionId) { mutableListOf() }.add(connection)
    }

    fun unregisterConnection(sessionId: String, connection: Connection) {
        sessions[sessionId]?.remove(connection)
    }

   suspend fun broadcastMessage(sessionId: String, message: String) {
        sessions[sessionId]?.forEach {
                it.send(message)
        }
    }

    suspend fun getMessagesBySessionId(sessionId: String): List<Message> {
        return messageDataSource.getMessagesBySessionId(sessionId)
    }
}