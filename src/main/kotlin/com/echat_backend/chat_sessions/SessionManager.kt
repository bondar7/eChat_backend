package com.echat_backend.chat_sessions

import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.models.Message

class SessionManager(
   private val messageDataSource: MessageDataSource,
) {
    private val sessions = mutableMapOf<String, MutableList<Connection>>()

    fun registerConnection(sessionId: String, connection: Connection) {
        // Перевірка, чи з'єднання вже існує для даного користувача в сесії
        val existingConnections = sessions[sessionId]
        if (existingConnections != null) {
            val existingConnection = existingConnections.find { it.userId == connection.userId }
            if (existingConnection != null) {
                // З'єднання вже існує, можливо, ви хочете додати додаткову логіку або повернути помилку
                // Наприклад, можливо, вам не потрібно створювати нове з'єднання або видаляти існуюче
                return
            }
        }
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