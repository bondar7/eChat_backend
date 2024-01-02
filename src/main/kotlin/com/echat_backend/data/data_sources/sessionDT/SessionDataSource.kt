package com.echat_backend.data.data_sources.sessionDT

import com.echat_backend.data.models.Session
import org.bson.types.ObjectId

interface SessionDataSource {
    suspend fun createSession(
        user1Id: String,
        user2Id: String
    ): String

    suspend fun getSessionById(sessionId: String): Session?
}