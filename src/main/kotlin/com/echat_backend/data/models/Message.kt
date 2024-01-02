package com.echat_backend.data.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.sql.Timestamp

@Serializable
data class Message(
    val sessionId: String,
    val senderId: String,
    val senderUsername: String,
    val content: String,
    val timestamp: Long,
    @BsonId val id: String = ObjectId().toString()
)
