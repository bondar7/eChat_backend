package com.echat_backend.data.data_sources.messageDT

import com.echat_backend.data.models.Message


interface MessageDataSource {
    suspend fun insertMessage(message: Message)

    suspend fun getMessagesBySessionId(sessionId: String): List<Message>
}