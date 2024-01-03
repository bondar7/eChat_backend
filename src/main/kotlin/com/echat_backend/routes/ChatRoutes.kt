package com.echat_backend.routes

import com.echat_backend.chat_sessions.Connection
import com.echat_backend.chat_sessions.SessionManager
import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.data_sources.sessionDT.SessionDataSource
import com.echat_backend.data.data_sources.userDT.UserDataSource
import com.echat_backend.data.models.Message
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.chatSocket(
    sessionManager: SessionManager,
    messageDataSource: MessageDataSource,
    userDataSource: UserDataSource
) {
    webSocket("/chat/{sessionId}/{currentUserId}") {
        val sessionId = call.parameters["sessionId"] ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.NORMAL,
                "No session ID"
            )
        )

        // Обробка нового WebSocket з'єднання
        val currentUserId = call.parameters["currentUserId"] // Функція для отримання поточного користувача
        if (currentUserId != null) {
            val currentUsername = userDataSource.getUserById(currentUserId)?.username
            if (currentUsername != null) {
                val connection = Connection(currentUserId, this)

                // Реєстрація нового з'єднання у сесії
                sessionManager.registerConnection(sessionId, connection)

                try {
                    // Очікування повідомлень від клієнта
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                // Обробка отриманого текстового повідомлення
                                val message = frame.readText()
                                println("RECEIVED MESSAGE: $message")
                                handleMessage(
                                    sessionId,
                                    currentUserId,
                                    currentUsername,
                                    message,
                                    messageDataSource,
                                    sessionManager
                                )
                            }

                            else -> {}
                            // Інші типи повідомлень можна обробити аналогічно
                        }
                    }
                } finally {
                    // При завершенні WebSocket з'єднання видалимо зареєстроване з'єднання
                    sessionManager.unregisterConnection(sessionId, connection)
                }
            }
        }
    }
}

private suspend fun handleMessage(
    sessionId: String,
    senderUserId: String,
    senderUsername: String,
    message: String,
    messageDataSource: MessageDataSource,
    sessionManager: SessionManager
) {
    val messageEntity = Message(
        sessionId,
        senderUserId,
        senderUsername,
        message,
        System.currentTimeMillis()
    )
    // Зберегти повідомлення у базі даних, якщо потрібно
    messageDataSource.insertMessage(
        messageEntity
    )

    val jsonMessage = Json.encodeToString(messageEntity)

    // Відправити повідомлення всім учасникам сесії
    sessionManager.broadcastMessage(sessionId, jsonMessage)
}

fun Route.getMessagesBySessionId(
    sessionManager: SessionManager
) {
    get(path = "get-messages-by-sessionID") {
        val sessionId = call.parameters["sessionId"] ?: run {
            call.respond(HttpStatusCode.BadRequest, emptyList<Message>())
            return@get
        }
        call.respond(HttpStatusCode.OK, sessionManager.getMessagesBySessionId(sessionId))
    }
}