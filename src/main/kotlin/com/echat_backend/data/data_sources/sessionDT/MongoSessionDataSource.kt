package com.echat_backend.data.data_sources.sessionDT

import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.data_sources.userDT.UserDataSource
import com.echat_backend.data.models.ChatSession
import com.echat_backend.data.models.Session
import com.echat_backend.data.responses.Person
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.or
import java.text.SimpleDateFormat
import java.util.*

class MongoSessionDataSource(
    db: CoroutineDatabase
) : SessionDataSource {

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
            val session = sessions.findOne(Session::id eq sessionId)
            session
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getSessionsByUserId(
        userId: String,
        userDataSource: UserDataSource,
        messageDataSource: MessageDataSource
    ): List<ChatSession> {
        return try {
            val foundSession = sessions.find(
                or(
                    and(Session::user1Id eq userId),
                    and(Session::user2Id eq userId)
                )
            ).toList()

            val newList = foundSession.map {
                val sndUserId = if (it.user1Id == userId) {
                    it.user2Id
                } else {
                    it.user1Id
                }

                val foundSndUserById = userDataSource.getUserById(sndUserId)

                if (foundSndUserById != null) {
                    val person = Person(
                        foundSndUserById.id,
                        foundSndUserById.username,
                        foundSndUserById.name ?: foundSndUserById.username,
                        foundSndUserById.avatar,
                        foundSndUserById.userBio
                    )

                    val lastMessage = messageDataSource.getLastMessageBySessionId(it.id)

                    ChatSession(
                        sessionId = it.id,
                        user = person,
                        lastMessage = lastMessage.text ?: "Image",
                        lastMessageSentTime = lastMessage.timestamp
                    )
                } else {
                    null // Return null for cases where the user is not found
                }
            }
            // Filter out null values and return the list of ChatSessions
            newList.filterNotNull()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}