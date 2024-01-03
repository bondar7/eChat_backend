package com.echat_backend.data.data_sources.messageDT

import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.models.Message
import com.echat_backend.data.models.Session
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.descending
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

    override suspend fun getLastMessageBySessionId(sessionId: String): Message {
        return try {
            val sessionMessages = messages.find(Message::sessionId eq sessionId)
                .sort(descending(Message::timestamp))
                .limit(1)
                .toList()

            if (sessionMessages.isNotEmpty()) {
                // Вертаємо останнє повідомлення
                sessionMessages[0]
            } else {
                // Якщо список порожній, можна повернути дефолтне значення або обробити відповідним чином
                // Наприклад, кинути виняток або повернути порожнє повідомлення.
                // Тут ви можете визначити, як краще вам обробити цю ситуацію.
                throw NoSuchElementException("No messages found for session with id: $sessionId")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Тут також можна визначити, як обробити виняток або повернути дефолтне значення
            // в залежності від ваших потреб.
            throw e
        }
    }
}