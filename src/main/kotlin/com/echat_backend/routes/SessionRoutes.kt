package com.echat_backend.routes

import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.data_sources.sessionDT.SessionDataSource
import com.echat_backend.data.data_sources.userDT.UserDataSource
import com.echat_backend.data.models.ChatSession
import com.echat_backend.data.requests.CreateSessionRequest
import com.echat_backend.data.responses.CreateChatSessionResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createSession(
    sessionDataSource: SessionDataSource,
) {
    post("create-session") {
        val request = call.receiveOrNull<CreateSessionRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val sessionId = sessionDataSource.createSession(request.user1Id, request.user2Id)
        println("SESSION ID: $sessionId")
        call.respond(HttpStatusCode.OK, CreateChatSessionResponse(sessionId))
    }
}

fun Route.getSessionsByUserId(
    sessionDataSource: SessionDataSource,
    userDataSource: UserDataSource,
    messageDataSource: MessageDataSource
) {
    get("get-sessions-by-userID") {
        val userId = call.parameters["userId"] ?: run {
            call.respond(HttpStatusCode.BadRequest, emptyList<ChatSession>())
            return@get
        }
        val chatSessions = sessionDataSource.getSessionsByUserId(userId, userDataSource, messageDataSource)
        println("RECEIVED ID: $userId")
        println("FOUND SESSIONS: $chatSessions")
        call.respond(HttpStatusCode.OK, chatSessions)
    }
}