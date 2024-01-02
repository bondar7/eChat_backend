package com.echat_backend.room

import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.models.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {

    private val chatSessions = ConcurrentHashMap<String, ChatSession>()
}