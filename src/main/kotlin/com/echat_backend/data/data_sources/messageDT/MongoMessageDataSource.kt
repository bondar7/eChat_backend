package com.echat_backend.data.data_sources.messageDT

import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.models.Message
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoMessageDataSource(
    db: CoroutineDatabase
): MessageDataSource {

    val messages = db.getCollection<Message>()
    override suspend fun insertMessage(
        message: Message
    ) {
        try {
            messages.insertOne(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getMessagesBySessionId(sessionId: String): List<Message> {
        return try {
           messages.find(Message::sessionId eq sessionId).toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}