package com.echat_backend.routes

import com.echat_backend.data.data_sources.sessionDT.SessionDataSource
import com.echat_backend.data.requests.CreateSessionRequest
import com.echat_backend.data.requests.SessionResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createSession(
    sessionDataSource: SessionDataSource
) {
    post("create-session") {
        val request = call.receiveOrNull<CreateSessionRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val sessionId = sessionDataSource.createSession(request.user1Id, request.user2Id)
        println("SESSION ID: $sessionId")
            call.respond(HttpStatusCode.OK, SessionResponse(sessionId))
    }
}