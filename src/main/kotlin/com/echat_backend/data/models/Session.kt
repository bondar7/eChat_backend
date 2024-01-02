package com.echat_backend.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.sql.Timestamp


data class Session(
    val user1Id: String,
    val user2Id: String,
    val createdAt: Long,
    @BsonId val id: String = ObjectId().toString(),
)
