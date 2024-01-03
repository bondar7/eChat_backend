package com.echat_backend.data.data_sources.messageDT

import com.echat_backend.data.models.Message
import com.echat_backend.data.models.Session


interface MessageDataSource {
    suspend fun insertMessage(message: Message)

    suspend fun getMessagesBySessionId(sessionId: String): List<Message>

    suspend fun getLastMessageBySessionId(sessionId: String): Message
}