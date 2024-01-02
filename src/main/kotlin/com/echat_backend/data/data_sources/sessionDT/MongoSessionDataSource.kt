package com.echat_backend.data.data_sources.sessionDT

import com.echat_backend.data.models.Session
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.or

class MongoSessionDataSource(
    db: CoroutineDatabase
): SessionDataSource {

    private val sessions = db.getCollection<Session>()
    override suspend fun createSession(user1Id: String, user2Id: String): String {
        return try {
            val foundSession = sessions.findOne(
                or(
                    and(Session::user1Id eq user1Id, Session::user2Id eq user2Id),
                    and(Session::user1Id eq user2Id, Session::user2Id eq user1Id)
                )
            )
             if (foundSession == null) {
                 val newSession = Session(
                     user1Id = user1Id,
                     user2Id = user2Id,
                     createdAt = System.currentTimeMillis()
                 )
                 sessions.insertOne(newSession)
                 newSession.id
             } else {
                 foundSession.id
             }
        } catch (e: Exception) {
            e.printStackTrace()
             ""
        }
    }

    override suspend fun getSessionById(sessionId: String): Session? {
       return try {
            val session = sessions.findOne(Session::id eq  sessionId)
           session
        } catch (e: Exception) {
            e.printStackTrace()
           null
        }
    }
}
