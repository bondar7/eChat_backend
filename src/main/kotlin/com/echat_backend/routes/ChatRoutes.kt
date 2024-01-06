package com.echat_backend.routes

import com.echat_backend.chat_sessions.Connection
import com.echat_backend.chat_sessions.SessionManager
import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.data_sources.sessionDT.SessionDataSource
import com.echat_backend.data.data_sources.userDT.UserDataSource
import com.echat_backend.data.models.Message
import com.echat_backend.data.remote.OneSignalService
import com.echat_backend.data.remote.dto.Notification
import com.echat_backend.data.remote.dto.NotificationMessage
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
    sessionDataSource: SessionDataSource,
    messageDataSource: MessageDataSource,
    userDataSource: UserDataSource,
    oneSignalService: OneSignalService
) {
    webSocket("/chat/{sessionId}/{currentUserId}") {
        val sessionId = call.parameters["sessionId"] ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.NORMAL,
                "No session ID"
            )
        )
        val currentSession = sessionDataSource.getSessionById(sessionId)
        // Обробка нового WebSocket з'єднання
        val currentUserId = call.parameters["currentUserId"] // Функція для отримання поточного користувача
        if (currentUserId != null) {
            val currentUsername = userDataSource.getUserById(currentUserId)?.username
            if (currentUsername != null && currentSession != null) {
                val connection = Connection(currentUserId, this)

                // Реєстрація нового з'єднання у сесії
                sessionManager.registerConnection(sessionId, connection)

                val receiverUserId =
                    if (currentSession.user1Id != currentUserId) currentSession.user1Id
                    else currentSession.user2Id

                try {
                    // Очікування повідомлень від клієнта
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                // Обробка отриманого текстового повідомлення
                                val message = frame.readText()
                                println("RECEIVED MESSAGE: $message")
                                handleMessage(
                                    sessionId = sessionId,
                                    senderUserId = currentUserId,
                                    receiverUserId = receiverUserId,
                                    senderUsername = currentUsername,
                                    messageText = message,
                                    messageImage = null,
                                    messageDataSource = messageDataSource,
                                    sessionManager = sessionManager,
                                    oneSignalService = oneSignalService
                                )
                            }

                            is Frame.Binary -> {
                                // Обробка отриманого binary message
                                val byteArray = frame.readBytes()
                                println("RECEIVED BYTES: $byteArray")
                                handleMessage(
                                    sessionId = sessionId,
                                    senderUserId = currentUserId,
                                    receiverUserId = receiverUserId,
                                    senderUsername = currentUsername,
                                    messageText = null,
                                    messageImage = byteArray,
                                    messageDataSource = messageDataSource,
                                    sessionManager = sessionManager,
                                    oneSignalService = oneSignalService
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
    receiverUserId: String,
    senderUsername: String,
    messageText: String?,
    messageImage: ByteArray?,
    messageDataSource: MessageDataSource,
    sessionManager: SessionManager,
    oneSignalService: OneSignalService
) {
    val messageEntity = Message(
        sessionId = sessionId,
        senderId = senderUserId,
        senderUsername = senderUsername,
        text = messageText,
        image = messageImage,
        timestamp = System.currentTimeMillis()
    )
    // Зберегти повідомлення у базі даних, якщо потрібно
    messageDataSource.insertMessage(
        messageEntity
    )

    val jsonMessage = Json.encodeToString(messageEntity)
    val notification = Notification(
        includeExternalUserIds = listOf(receiverUserId),
        headings = NotificationMessage(senderUsername),
        contents = NotificationMessage(messageText ?: "Image"),
        appId = System.getenv("ONESINGLE_APP_ID")
    )
    // Відправити повідомлення всім учасникам сесії і відправка push-notification певному користувачеві,
    // якщо він не є в чаті(немає веб сокет з'єднання)
    sessionManager.broadcastMessage(
        sessionId,
        receiverUserId,
        jsonMessage,
        notification,
        oneSignalService
    )
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